package com.cast4it.desafio.cast.service;

import com.cast4it.desafio.cast.dto.ContaDTO;
import com.cast4it.desafio.cast.dto.ExtratoResponseDTO;
import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.entity.Conta;
import com.cast4it.desafio.cast.enums.TipoTransacao;
import com.cast4it.desafio.cast.exception.*;
import com.cast4it.desafio.cast.mapper.ContaMapper;
import com.cast4it.desafio.cast.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoService transacaoService;

    @Mock
    private ContaMapper contaMapper;

    @InjectMocks
    private ContaService contaService;

    @Nested
    @DisplayName("Testes de Criação de Conta")
    class CriacaoConta {

        @Test
        @DisplayName("Deve criar conta com sucesso quando CPF não existe")
        void deveCriarContaComSucesso() {
            var titular = "João Silva";
            var cpf = "123.456.789-01";
            var cpfLimpo = "12345678901";

            when(contaRepository.existsByCpf(cpfLimpo)).thenReturn(false);
            when(contaRepository.existsByNumero(anyString())).thenReturn(false);

            when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));
            when(contaMapper.toDTO(any(Conta.class))).thenReturn(new ContaDTO(1L, cpfLimpo, titular, BigDecimal.ZERO, "12345678901"));

            var resultado = contaService.criarConta(titular, cpf);

            assertNotNull(resultado);
            assertEquals(titular, resultado.titular());
            verify(contaRepository).save(any(Conta.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando CPF já estiver cadastrado")
        void deveLancarExcecaoCpfExistente() {
            var cpf = "12345678901";
            when(contaRepository.existsByCpf(cpf)).thenReturn(true);

            assertThrows(CpfJaCadastradoException.class, () ->
                    contaService.criarConta("Teste", cpf)
            );
        }
    }

    @Nested
    @DisplayName("Testes de Operações Financeiras")
    class OperacoesFinanceiras {

        @Test
        @DisplayName("Deve realizar transferência com sucesso entre duas contas")
        void deveTransferirComSucesso() {
            var valor = new BigDecimal("100.00");
            var origem = Conta.builder().id(1L).titular("Origem").saldo(new BigDecimal("500.00")).build();
            var destino = Conta.builder().id(2L).titular("Destino").saldo(new BigDecimal("200.00")).build();

            when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origem));
            when(contaRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(destino));

            when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));

            contaService.transferir(1L, 2L, valor);

            assertEquals(new BigDecimal("400.00"), origem.getSaldo());
            assertEquals(new BigDecimal("300.00"), destino.getSaldo());

            verify(transacaoService, times(2)).registrarTransacao(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar SaldoInsuficienteException quando origem não tem saldo")
        void deveFalharTransferenciaSaldoInsuficiente() {
            var valor = new BigDecimal("1000.00");
            var origem = Conta.builder().id(1L).saldo(new BigDecimal("50.00")).build();

            when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origem));
            when(contaRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(Conta.builder().id(2L).build()));

            assertThrows(SaldoInsuficienteException.class, () ->
                    contaService.transferir(1L, 2L, valor)
            );
        }
    }

    @Test
    @DisplayName("Deve buscar conta por CPF com sucesso")
    void deveBuscarPorCpfComSucesso() {
        String cpf = "12345678901";
        Conta conta = Conta.builder().id(1L).cpf(cpf).titular("Welsson").build();
        ContaDTO dto = new ContaDTO(1L, "12345-6", "Welsson", BigDecimal.ZERO, cpf);

        when(contaRepository.findByCpf(cpf)).thenReturn(Optional.of(conta));
        when(contaMapper.toDTO(conta)).thenReturn(dto);

        ContaDTO resultado = contaService.buscarPorCpf(cpf);

        assertNotNull(resultado);
        assertEquals(cpf, resultado.cpf());
        verify(contaRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve listar contas paginadas")
    void deveListarPaginado() {
        Pageable pageable = PageRequest.of(0, 10);
        Conta conta = Conta.builder().id(1L).build();
        Page<Conta> page = new PageImpl<>(List.of(conta));

        when(contaRepository.findAll(pageable)).thenReturn(page);
        when(contaMapper.toDTO(any())).thenReturn(new ContaDTO(1L, "1-1", "T", BigDecimal.ZERO, "123"));

        Page<ContaDTO> resultado = contaService.listarPaginado(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        verify(contaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve debitar valor com sucesso")
    void deveDebitarComSucesso() {
        Long contaId = 1L;
        BigDecimal valorSaque = new BigDecimal("50.00");
        Conta conta = Conta.builder().id(contaId).saldo(new BigDecimal("100.00")).build();

        when(contaRepository.findByIdForUpdate(contaId)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));
        when(contaMapper.toDTO(any())).thenReturn(new ContaDTO(1L, "1-1", "T", new BigDecimal("50.00"), "123"));

        ContaDTO resultado = contaService.debitar(contaId, valorSaque);

        assertEquals(new BigDecimal("50.00"), conta.getSaldo());
        verify(transacaoService).registrarTransacao(eq(conta), eq(valorSaque), eq(TipoTransacao.DEBITO), any(), any(), any());
    }

    @Test
    @DisplayName("Deve gerar extrato completo")
    void deveGerarExtratoCompleto() {
        Long contaId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Conta conta = Conta.builder().id(contaId).saldo(new BigDecimal("200.00")).build();
        Page<TransacaoDTO> transacoesPage = new PageImpl<>(List.of());

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(transacaoService.buscarTransacoesPaginadas(contaId, pageable)).thenReturn(transacoesPage);

        ExtratoResponseDTO resultado = contaService.gerarExtratoCompleto(contaId, pageable);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("200.00"), resultado.saldoFinal());
        verify(transacaoService).buscarTransacoesPaginadas(contaId, pageable);
    }

    @Test
    @DisplayName("Deve lançar ValorInvalidoException quando valor for negativo ou zero")
    void deveLancarExcecaoValorInvalido() {
        BigDecimal valorZero = BigDecimal.ZERO;
        assertThrows(ValorInvalidoException.class, () ->
                contaService.creditar(1L, valorZero)
        );

        BigDecimal valorNegativo = new BigDecimal("-10.00");
        assertThrows(ValorInvalidoException.class, () ->
                contaService.creditar(1L, valorNegativo)
        );

        assertThrows(ValorInvalidoException.class, () ->
                contaService.creditar(1L, null)
        );
    }

    @Test
    @DisplayName("Deve lançar ContasIdenticasException ao transferir para a mesma conta")
    void deveLancarExcecaoContasIdenticas() {
        Long idMesmaConta = 1L;
        BigDecimal valor = new BigDecimal("50.00");

        assertThrows(ContasIdenticasException.class, () ->
                contaService.transferir(idMesmaConta, idMesmaConta, valor)
        );
    }

    @Test
    @DisplayName("Deve lançar ContaNaoEncontradaException quando ID for inexistente no debito")
    void deveLancarExcecaoContaInexistenteNoDebito() {
        when(contaRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () ->
                contaService.debitar(99L, new BigDecimal("10.00"))
        );
    }


}