package com.cast4it.desafio.cast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para operações de transferência entre contas.
 * Contém as referências para as contas de débito e crédito.
 */
@Schema(description = "Dados necessários para realizar uma transferência entre duas contas distintas")
public record TransferenciaDTO(

        @Schema(description = "ID da conta que enviará o valor (será debitada)", example = "1")
        @NotNull(message = "Conta de origem é obrigatória")
        Long origemId,

        @Schema(description = "ID da conta que receberá o valor (será creditada)", example = "2")
        @NotNull(message = "Conta de destino é obrigatória")
        Long destinoId,

        @Schema(description = "Valor monetário a ser transferido", example = "500.00")
        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor
) {}