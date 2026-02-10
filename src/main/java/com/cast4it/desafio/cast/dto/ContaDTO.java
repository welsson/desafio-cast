package com.cast4it.desafio.cast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO para representação detalhada de uma conta bancária.
 * Utilizado nas respostas de listagem e busca por ID/CPF.
 */
@Schema(description = "Representação dos dados de uma conta bancária para retorno da API")
public record ContaDTO(

        @Schema(description = "ID único gerado pelo banco de dados", example = "1")
        Long id,

        @Schema(description = "Número da conta gerado automaticamente (formato XXXXX-Y)", example = "54321-9")
        String numero,

        @Schema(description = "Nome do titular da conta", example = "João da Silva")
        String titular,

        @Schema(description = "Saldo atual da conta formatado como string para evitar perda de precisão", example = "1500.50")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal saldo,

        @Schema(description = "CPF do titular (apenas números)", example = "12345678900")
        String cpf

) {}