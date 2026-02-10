package com.cast4it.desafio.cast.service;

import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.entity.Conta;
import com.cast4it.desafio.cast.entity.Transacao;
import com.cast4it.desafio.cast.enums.TipoTransacao;
import com.cast4it.desafio.cast.mapper.TransacaoMapper;
import com.cast4it.desafio.cast.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelo registro e consulta do histórico de transações.
 * Garante a imutabilidade dos registros financeiros para fins de auditoria.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    /**
     * Registra uma nova movimentação financeira no banco de dados.
     * * @param conta A conta principal da transação.
     * @param valor O montante da operação.
     * @param tipo O tipo da transação (CREDITO, DEBITO, TRANSFERENCIA, etc).
     * @param contraparteNome Nome do terceiro envolvido (ou "SISTEMA").
     * @param contraparteConta Número da conta do terceiro envolvido.
     * @param codigoOperacao Identificador único (UUID) que vincula transações correlacionadas.
     * @return TransacaoDTO representando o registro salvo.
     */
    @Transactional
    public TransacaoDTO registrarTransacao(
            Conta conta,
            BigDecimal valor,
            TipoTransacao tipo,
            String contraparteNome,
            String contraparteConta,
            String codigoOperacao) {

        log.debug("Registrando {} na conta {}: R$ {} [Protocolo: {}]",
                tipo, conta.getNumero(), valor, codigoOperacao);

        Transacao transacao = Transacao.builder()
                .conta(conta)
                .valor(valor)
                .tipo(tipo)
                .contraparteNome(contraparteNome)
                .contraparteConta(contraparteConta)
                .codigoOperacao(codigoOperacao)
                .dataHora(LocalDateTime.now())
                .build();

        Transacao transacaoSalva = transacaoRepository.save(transacao);
        return transacaoMapper.toDTO(transacaoSalva);
    }

    /**
     * Recupera o histórico de transações de forma paginada para uma conta específica.
     * Os registros são ordenados de forma decrescente pela data (mais recentes primeiro).
     */
    @Transactional(readOnly = true)
    public Page<TransacaoDTO> buscarTransacoesPaginadas(Long contaId, Pageable pageable) {
        log.info("Buscando transações paginadas para a conta ID: {}", contaId);

        return transacaoRepository.findByContaId(contaId, pageable)
                .map(transacaoMapper::toDTO);
    }

    /**
     * Lista todas as transações de uma conta sem paginação.
     * @deprecated Recomenda-se o uso de buscarTransacoesPaginadas para evitar sobrecarga de memória.
     */
    @Deprecated
    @Transactional(readOnly = true)
    public List<TransacaoDTO> listarPorConta(Long contaId) {
        log.warn("Chamada de listagem de transações sem paginação para conta ID: {}", contaId);
        List<Transacao> listaTransacoes = transacaoRepository.findByContaIdOrderByDataHoraDesc(contaId);
        return listaTransacoes.stream()
                .map(transacaoMapper::toDTO)
                .toList();
    }


}