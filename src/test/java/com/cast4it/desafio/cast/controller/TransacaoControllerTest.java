package com.cast4it.desafio.cast.controller;

import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.enums.TipoTransacao;
import com.cast4it.desafio.cast.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService transacaoService;

    @Test
    @DisplayName("Deve listar transações detalhadas por ID da conta")
    void listarPorConta_DeveRetornarListaDeTransacoes() throws Exception {
        var contaId = 1L;
        var codigoOperacao = UUID.randomUUID().toString();

        var transacao = new TransacaoDTO(
                105L,
                new BigDecimal("150.00"),
                TipoTransacao.TRANSFERENCIA_RECEBIDA,
                LocalDateTime.now(),
                "12345-6",
                "Maria Oliveira",
                "67890-1",
                codigoOperacao
        );

        when(transacaoService.listarPorConta(contaId)).thenReturn(List.of(transacao));

        mockMvc.perform(get("/v1/transacoes/conta/{id}", contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(105))
                .andExpect(jsonPath("$[0].valor").value(150.00))
                .andExpect(jsonPath("$[0].tipo").value("TRANSFERENCIA_RECEBIDA"))
                .andExpect(jsonPath("$[0].contraparteNome").value("Maria Oliveira"))
                .andExpect(jsonPath("$[0].codigoOperacao").value(codigoOperacao))
                .andExpect(jsonPath("$[0].contaNumero").value("12345-6"));
    }

    @Test
    @DisplayName("Deve retornar 200 e lista vazia quando não houver movimentações")
    void listarPorConta_DeveRetornarVazio() throws Exception {
        when(transacaoService.listarPorConta(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/v1/transacoes/conta/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}