package com.cast4it.desafio.cast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;

/**
 * DTO de resposta para consulta de extrato.
 * Consolida o resumo financeiro do período e a lista detalhada de movimentações.
 */
@Schema(description = "Resposta consolidada contendo saldos e histórico de transações paginado")
public record ExtratoResponseDTO(

        @Schema(description = "Saldo da conta no início do período consultado", example = "1000.00")
        BigDecimal saldoInicial,

        @Schema(description = "Saldo atual da conta (saldo disponível)", example = "1250.75")
        BigDecimal saldoFinal,

        @Schema(description = "Objeto de paginação contendo a lista de transações e metadados (total de páginas, elementos, etc.)")
        Page<TransacaoDTO> transacoes
) {}