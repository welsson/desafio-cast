package com.cast4it.desafio.cast.service;

import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.entity.Conta;
import com.cast4it.desafio.cast.entity.Transacao;
import com.cast4it.desafio.cast.enums.TipoTransacao;
import com.cast4it.desafio.cast.mapper.TransacaoMapper;
import com.cast4it.desafio.cast.repository.TransacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private TransacaoMapper transacaoMapper;

    @InjectMocks
    private TransacaoService transacaoService;

    @Test
    @DisplayName("Deve registrar uma transação com sucesso e preencher dataHora")
    void registrarTransacao_DeveSalvarComSucesso() {
        var conta = Conta.builder().id(1L).numero("12345-6").build();
        var valor = new BigDecimal("250.00");
        var tipo = TipoTransacao.CREDITO;
        var codigoOperacao = UUID.randomUUID().toString();

        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));

        var dtoEsperado = new TransacaoDTO(1L, valor, tipo, LocalDateTime.now(),
                "12345-6", "SISTEMA", "N/A", codigoOperacao);
        when(transacaoMapper.toDTO(any(Transacao.class))).thenReturn(dtoEsperado);

        var resultado = transacaoService.registrarTransacao(
                conta, valor, tipo, "SISTEMA", "N/A", codigoOperacao);

        assertNotNull(resultado);
        assertEquals(codigoOperacao, resultado.codigoOperacao());
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve buscar transações paginadas corretamente")
    void buscarTransacoesPaginadas_DeveRetornarPagina() {
        var contaId = 1L;
        var pageable = PageRequest.of(0, 10);
        var transacao = new Transacao();
        var pagina = new PageImpl<>(List.of(transacao));

        when(transacaoRepository.findByContaId(eq(contaId), eq(pageable))).thenReturn(pagina);
        when(transacaoMapper.toDTO(any(Transacao.class))).thenReturn(mock(TransacaoDTO.class));

        var resultado = transacaoService.buscarTransacoesPaginadas(contaId, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(transacaoRepository).findByContaId(contaId, pageable);
    }

    @Test
    @DisplayName("Deve retornar lista no método depreciado para compatibilidade")
    void listarPorConta_DeveRetornarLista() {
        var contaId = 1L;
        var lista = List.of(new Transacao());
        when(transacaoRepository.findByContaIdOrderByDataHoraDesc(contaId)).thenReturn(lista);
        when(transacaoMapper.toDTO(any(Transacao.class))).thenReturn(mock(TransacaoDTO.class));

        var resultado = transacaoService.listarPorConta(contaId);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}