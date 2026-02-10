package com.cast4it.desafio.cast.service;

import com.cast4it.desafio.cast.dto.*;
import com.cast4it.desafio.cast.entity.Conta;
import com.cast4it.desafio.cast.enums.TipoTransacao;
import com.cast4it.desafio.cast.exception.*;
import com.cast4it.desafio.cast.mapper.ContaMapper;
import com.cast4it.desafio.cast.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

/**
 * Serviço responsável por gerenciar o ciclo de vida das contas bancárias
 * e orquestrar operações financeiras de crédito, débito e transferência.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoService transacaoService;
    private final ContaMapper contaMapper;

    /**
     * Lista todas as contas cadastradas com suporte a paginação.
     */
    @Transactional(readOnly = true)
    public Page<ContaDTO> listarPaginado(Pageable pageable) {
        log.debug("Listando contas paginadas: {}", pageable);
        return contaRepository.findAll(pageable).map(contaMapper::toDTO);
    }

    /**
     * Cria uma nova conta bancária.
     * Valida a existência do CPF e gera um número de conta único.
     *
     * @param titular Nome do proprietário da conta.
     * @param cpf CPF do titular (apenas números).
     * @return ContaDTO criada.
     * @throws CpfJaCadastradoException caso o CPF já possua uma conta.
     */
    @Transactional
    public ContaDTO criarConta(String titular, String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        log.info("Iniciando criação de conta para o CPF: {}", cpfLimpo);

        if (contaRepository.existsByCpf(cpfLimpo)) {
            log.warn("Tentativa de criação de conta com CPF já existente: {}", cpfLimpo);
            throw new CpfJaCadastradoException(cpfLimpo);
        }

        String numeroConta;
        do {
            numeroConta = gerarNumeroConta();
        } while (contaRepository.existsByNumero(numeroConta));

        Conta conta = Conta.builder()
                .numero(numeroConta)
                .titular(titular)
                .cpf(cpfLimpo)
                .saldo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        Conta contaSalva = contaRepository.save(conta);
        log.info("Conta {} criada com sucesso para o titular {}", numeroConta, titular);
        return contaMapper.toDTO(contaSalva);
    }

    /**
     * Realiza um depósito em conta e registra a transação.
     */
    @Transactional
    public ContaDTO creditar(Long contaId, BigDecimal valor) {
        log.info("Processando depósito de R$ {} na conta ID {}", valor, contaId);
        validarValorPositivo(valor);

        Conta conta = executarCreditoSemRegistrar(contaId, valor);

        transacaoService.registrarTransacao(
                conta, valor, TipoTransacao.CREDITO,
                "DEPÓSITO", "CAIXA ELETRÔNICO", UUID.randomUUID().toString()
        );

        log.info("Depósito finalizado com sucesso. Novo saldo da conta {}: R$ {}", contaId, conta.getSaldo());
        return contaMapper.toDTO(conta);
    }

    /**
     * Realiza um saque em conta após validar saldo suficiente.
     */
    @Transactional
    public ContaDTO debitar(Long contaId, BigDecimal valor) {
        log.info("Processando saque de R$ {} na conta ID {}", valor, contaId);
        validarValorPositivo(valor);

        Conta conta = executarDebitoSemRegistrar(contaId, valor);

        transacaoService.registrarTransacao(
                conta, valor, TipoTransacao.DEBITO,
                "SAQUE", "CAIXA ELETRÔNICO", UUID.randomUUID().toString()
        );

        log.info("Saque finalizado com sucesso na conta ID {}", contaId);
        return contaMapper.toDTO(conta);
    }

    /**
     * Executa transferência entre contas com estratégia de bloqueio pessimista para evitar Deadlocks.
     * A operação segue o princípio de partida dobrada: registra um débito na origem e um crédito no destino.
     */
    @Transactional
    public void transferir(Long origemId, Long destinoId, BigDecimal valor) {
        log.info("Iniciando transferência: Origem={}, Destino={}, Valor=R$ {}", origemId, destinoId, valor);
        validarParametrosTransferencia(origemId, destinoId, valor);

        // Essa ordenção serve para prevenção de Deadlock
        OrdenacaoIds ids = ordenarIds(origemId, destinoId);

        log.debug("Adquirindo locks pessimistas na ordem: {}, {}", ids.primeiro(), ids.segundo());
        contaRepository.findByIdForUpdate(ids.primeiro());
        contaRepository.findByIdForUpdate(ids.segundo());

        Conta origem = executarDebitoSemRegistrar(origemId, valor);
        Conta destino = executarCreditoSemRegistrar(destinoId, valor);

        String codigoOperacao = UUID.randomUUID().toString();

        transacaoService.registrarTransacao(origem, valor, TipoTransacao.TRANSFERENCIA_ENVIADA,
                destino.getTitular(), destino.getNumero(), codigoOperacao);

        transacaoService.registrarTransacao(destino, valor, TipoTransacao.TRANSFERENCIA_RECEBIDA,
                origem.getTitular(), origem.getNumero(), codigoOperacao);

        log.info("Transferência concluída. Protocolo: {}", codigoOperacao);
    }

    /**
     * Gera o extrato consolidado com saldo atual e transações paginadas.
     */
    @Transactional(readOnly = true)
    public ExtratoResponseDTO gerarExtratoCompleto(Long contaId, Pageable pageable) {
        log.debug("Gerando extrato para conta ID {} com paginação {}", contaId, pageable);

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException(contaId));

        Page<TransacaoDTO> transacoes = transacaoService.buscarTransacoesPaginadas(contaId, pageable);

        return new ExtratoResponseDTO(BigDecimal.ZERO, conta.getSaldo(), transacoes);
    }

    /**
     * Busca os dados de uma conta através do CPF.
     */
    public ContaDTO buscarPorCpf(String cpf) {
        log.debug("Buscando conta pelo CPF: {}", cpf);
        return contaRepository.findByCpf(cpf.replaceAll("\\D", ""))
                .map(contaMapper::toDTO)
                .orElseThrow(() -> new CpfNaoEncontradoException(cpf));
    }

    /**
     * Atualiza o saldo da conta no banco de dados aplicando a operação matemática.
     */
    private Conta alterarSaldo(Long contaId, BigDecimal valor, TipoOperacao operacao) {
        Conta conta = contaRepository.findByIdForUpdate(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException(contaId));

        BigDecimal novoSaldo = switch (operacao) {
            case ADICAO -> conta.getSaldo().add(valor);
            case SUBTRACAO -> {
                if (conta.getSaldo().compareTo(valor) < 0) {
                    log.warn("Saldo insuficiente na conta {}: Saldo={}, Tentativa={}", contaId, conta.getSaldo(), valor);
                    throw new SaldoInsuficienteException();
                }
                yield conta.getSaldo().subtract(valor);
            }
        };

        conta.setSaldo(novoSaldo);
        return contaRepository.save(conta);
    }

    private Conta executarDebitoSemRegistrar(Long contaId, BigDecimal valor) {
        return alterarSaldo(contaId, valor, TipoOperacao.SUBTRACAO);
    }

    private Conta executarCreditoSemRegistrar(Long contaId, BigDecimal valor) {
        return alterarSaldo(contaId, valor, TipoOperacao.ADICAO);
    }

    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor deve ser maior que zero.");
        }
    }

    private void validarParametrosTransferencia(Long origemId, Long destinoId, BigDecimal valor) {
        if (origemId.equals(destinoId)) {
            throw new ContasIdenticasException("Não é possível transferir para a mesma conta.");
        }
        validarValorPositivo(valor);
    }

    private OrdenacaoIds ordenarIds(Long idA, Long idB) {
        return idA < idB ? new OrdenacaoIds(idA, idB) : new OrdenacaoIds(idB, idA);
    }

    private String gerarNumeroConta() {
        Random random = new Random();
        return String.format("%d-%d", 10000 + random.nextInt(90000), random.nextInt(10));
    }

    private record OrdenacaoIds(Long primeiro, Long segundo) {}
    private enum TipoOperacao { ADICAO, SUBTRACAO }
}