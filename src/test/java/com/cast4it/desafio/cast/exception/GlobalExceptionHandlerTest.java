package com.cast4it.desafio.cast.exception;

import com.cast4it.desafio.cast.controller.ContaController;
import com.cast4it.desafio.cast.service.ContaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
@DisplayName("Testes do Manipulador Global de Exceções")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    @Test
    @DisplayName("Deve capturar ContaNaoEncontradaException e retornar 404")
    void deveCapturarContaNaoEncontrada() throws Exception {
        when(contaService.gerarExtratoCompleto(any(), any()))
                .thenThrow(new ContaNaoEncontradaException(1L));

        mockMvc.perform(get("/v1/contas/1/extrato"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ContaNaoEncontrada"));
    }

    @Test
    @DisplayName("Deve capturar CpfNaoEncontradoException e retornar 404")
    void deveCapturarCpfNaoEncontrado() throws Exception {
        when(contaService.buscarPorCpf(anyString()))
                .thenThrow(new CpfNaoEncontradoException("12345678901"));

        mockMvc.perform(get("/v1/contas/cpf/12345678901"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CpfNaoEncontrado"));
    }

    @Test
    @DisplayName("Deve capturar ValorInvalidoException e retornar 400")
    void deveCapturarValorInvalido() throws Exception {
        when(contaService.creditar(any(), any()))
                .thenThrow(new ValorInvalidoException("Valor inválido"));

        String json = "{\"contaId\": 1, \"valor\": 10.0}";

        mockMvc.perform(post("/v1/contas/depositos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValorInvalido"))
                .andExpect(jsonPath("$.message").value("Valor inválido"));
    }

    @Test
    @DisplayName("Deve capturar ContasIdenticasException e retornar 422")
    void deveCapturarContasIdenticas() throws Exception {
        org.mockito.Mockito.doThrow(new ContasIdenticasException("Contas iguais"))
                .when(contaService).transferir(any(), any(), any());

        String json = "{\"origemId\": 1, \"destinoId\": 1, \"valor\": 100.00}";

        mockMvc.perform(post("/v1/contas/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("ContasIdenticas"));
    }

    @Test
    @DisplayName("Deve capturar Exception genérica e retornar 500")
    void deveCapturarErroGenerico() throws Exception {
        when(contaService.listarPaginado(any()))
                .thenThrow(new RuntimeException("Erro catastrófico"));

        mockMvc.perform(get("/v1/contas"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("InternalServerError"));
    }

    @Test
    @DisplayName("Deve capturar SaldoInsuficienteException e retornar 400")
    void deveCapturarSaldoInsuficiente() throws Exception {
        when(contaService.debitar(any(), any()))
                .thenThrow(new SaldoInsuficienteException());

        String json = "{\"contaId\": 1, \"valor\": 1000.00}";

        mockMvc.perform(post("/v1/contas/saques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("SaldoInsuficiente"))
                .andExpect(jsonPath("$.message").value("O saldo disponível na conta é insuficiente para realizar esta operação."));
    }

    @Test
    @DisplayName("Deve capturar DataIntegrityViolationException e retornar 409")
    void deveCapturarDataIntegrityViolation() throws Exception {
        when(contaService.criarConta(any(), any()))
                .thenThrow(new DataIntegrityViolationException("Erro de duplicidade"));

        String json = "{\"titular\": \"Welsson\", \"cpf\": \"12345678901\"}";

        mockMvc.perform(post("/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DataIntegrityViolation"));
    }

    @Test
    @DisplayName("Deve capturar CpfJaCadastradoException e retornar 409")
    void deveCapturarCpfJaCadastrado() throws Exception {
        when(contaService.criarConta(any(), any()))
                .thenThrow(new CpfJaCadastradoException("12345678901"));

        String json = "{\"titular\": \"Welsson\", \"cpf\": \"12345678901\"}";

        mockMvc.perform(post("/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CpfJaCadastrado"));
    }
}