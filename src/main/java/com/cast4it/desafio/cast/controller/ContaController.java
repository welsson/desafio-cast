package com.cast4it.desafio.cast.controller;

import com.cast4it.desafio.cast.dto.*;
import com.cast4it.desafio.cast.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Endpoints para gerenciamento de contas e operações bancárias")
@CrossOrigin(origins = "*")
public class ContaController {

    private final ContaService contaService;

    @Operation(summary = "Listar contas paginadas", description = "Retorna uma lista de contas com paginação e ordenação padrão por titular.")
    @GetMapping
    public ResponseEntity<Page<ContaDTO>> listarPaginado(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "titular", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(contaService.listarPaginado(pageable));
    }

    @Operation(summary = "Criar nova conta", description = "Cria uma conta bancária vinculada a um titular e CPF válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou CPF já existente")
    })
    @PostMapping
    public ResponseEntity<ContaDTO> criar(@Valid @RequestBody ContaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarConta(dto.titular(), dto.cpf()));
    }

    @Operation(summary = "Realizar depósito", description = "Incrementa o saldo de uma conta existente através do ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor do depósito inválido (ex: negativo ou zero)"),
            @ApiResponse(responseCode = "404", description = "Conta de destino não encontrada")
    })
    @PostMapping("/depositos")
    public ResponseEntity<ContaDTO> depositar(@Valid @RequestBody OperacaoDTO operacao) {
        return ResponseEntity.ok(contaService.creditar(operacao.contaId(), operacao.valor()));
    }

    @Operation(summary = "Realizar saque", description = "Deduz o valor do saldo da conta, validando se há saldo suficiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou dados da operação inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PostMapping("/saques")
    public ResponseEntity<ContaDTO> sacar(@Valid @RequestBody OperacaoDTO operacao) {
        return ResponseEntity.ok(contaService.debitar(operacao.contaId(), operacao.valor()));
    }

    @Operation(summary = "Transferência entre contas", description = "Transfere valores entre duas contas de forma atômica e segura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transferência concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente na conta de origem ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta de origem ou destino não encontrada"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (ex: transferência para a mesma conta)")
    })
    @PostMapping("/transferencias")
    public ResponseEntity<Void> transferir(@Valid @RequestBody TransferenciaDTO dto) {
        contaService.transferir(dto.origemId(), dto.destinoId(), dto.valor());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar extrato", description = "Retorna o histórico de movimentações da conta de forma paginada, incluindo saldos inicial e final.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID informado")
    })
    @GetMapping("/{id}/extrato")
    public ResponseEntity<ExtratoResponseDTO> buscarExtrato(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contaService.gerarExtratoCompleto(id, pageable));
    }

    @Operation(summary = "Buscar conta por CPF", description = "Localiza os dados simplificados da conta utilizando o CPF do titular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma conta encontrada para o CPF informado")
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ContaDTO> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(contaService.buscarPorCpf(cpf));
    }
}