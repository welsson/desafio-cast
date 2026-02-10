package com.cast4it.desafio.cast.controller;

import com.cast4it.desafio.cast.dto.*;
import com.cast4it.desafio.cast.exception.CpfNaoEncontradoException;
import com.cast4it.desafio.cast.service.ContaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContaService contaService;

    @Test
    @DisplayName("Deve listar contas paginadas com sucesso")
    void listarPaginado_DeveRetornarStatus200() throws Exception {
        ContaDTO conta = new ContaDTO(1L, "12345678901", "João Silva", BigDecimal.ZERO, "00896533433");
        PageImpl<ContaDTO> page = new PageImpl<>(List.of(conta));

        when(contaService.listarPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/v1/contas")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titular").value("João Silva"));
    }

    @Test
    @DisplayName("Deve criar uma nova conta com sucesso")
    void criar_DeveRetornarStatus201() throws Exception {
        ContaCreateDTO dto = new ContaCreateDTO("Maria Oliveira", "98765432100");
        ContaDTO response = new ContaDTO(2L, "98765432100", "Maria Oliveira", BigDecimal.ZERO, "98765432100");

        when(contaService.criarConta(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.cpf").value("98765432100"));
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso")
    void depositar_DeveRetornarStatus200() throws Exception {
        OperacaoDTO operacao = new OperacaoDTO(1L, new BigDecimal("100.00"));
        ContaDTO response = new ContaDTO(1L, "João", "123", new BigDecimal("100.00"), "00896533433");

        when(contaService.creditar(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/v1/contas/depositos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(100.00));
    }

    @Test
    @DisplayName("Deve realizar transferência com sucesso")
    void transferir_DeveRetornarStatus204() throws Exception {
        TransferenciaDTO dto = new TransferenciaDTO(1L, 2L, new BigDecimal("50.00"));

        mockMvc.perform(post("/v1/contas/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar extrato com sucesso")
    void buscarExtrato_DeveRetornarStatus200() throws Exception {
        ExtratoResponseDTO extrato = new ExtratoResponseDTO(
                BigDecimal.ZERO,
                BigDecimal.TEN,
                new PageImpl<>(List.of())
        );

        when(contaService.gerarExtratoCompleto(anyLong(), any())).thenReturn(extrato);

        mockMvc.perform(get("/v1/contas/1/extrato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoFinal").value(10));
    }

    @Test
    @DisplayName("Deve buscar conta por CPF e retornar 200")
    void buscarPorCpf_DeveRetornarContaComSucesso() throws Exception {
        var cpfEntrada = "123.456.789-01";
        var cpfLimpo = "12345678901";
        var response = new ContaDTO(1L, cpfLimpo, "João Silva", BigDecimal.ZERO, cpfLimpo);

        when(contaService.buscarPorCpf(cpfEntrada)).thenReturn(response);

        mockMvc.perform(get("/v1/contas/cpf/{cpf}", cpfEntrada)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(cpfLimpo))
                .andExpect(jsonPath("$.titular").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando o CPF não for encontrado")
    void buscarPorCpf_DeveRetornar404QuandoNaoExistir() throws Exception {
        var cpfInexistente = "00000000000";

        when(contaService.buscarPorCpf(cpfInexistente))
                .thenThrow(new CpfNaoEncontradoException(cpfInexistente));

        mockMvc.perform(get("/v1/contas/cpf/{cpf}", cpfInexistente))
                .andExpect(status().isNotFound());
    }
}