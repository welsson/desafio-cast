package com.cast4it.desafio.cast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para operações financeiras unitárias (Saque ou Depósito).
 */
@Schema(description = "Dados para realização de operações de crédito ou débito em uma conta")
public record OperacaoDTO(

        @Schema(description = "ID único da conta que receberá a operação", example = "1")
        @NotNull(message = "O ID da conta é obrigatório")
        Long contaId,

        @Schema(description = "Valor monetário da operação", example = "250.00")
        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor
) {}