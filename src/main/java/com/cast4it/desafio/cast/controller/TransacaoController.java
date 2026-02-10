package com.cast4it.desafio.cast.controller;

import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/transacoes")
@Tag(name = "Transações", description = "Endpoints para consulta do histórico de movimentações financeiras")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @Operation(
            summary = "Listar transações por conta",
            description = "Recupera o histórico completo de depósitos, saques e transferências vinculados a uma conta específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transações recuperada com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID da conta não encontrado")
    })
    @GetMapping("/conta/{id}")
    public ResponseEntity<List<TransacaoDTO>> listarPorConta(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.listarPorConta(id));
    }
}