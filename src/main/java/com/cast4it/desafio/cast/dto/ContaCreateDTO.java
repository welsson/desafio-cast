package com.cast4it.desafio.cast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de conta.
 * Inclui validações de Bean Validation e documentação para o Swagger UI.
 */
@Schema(description = "Dados necessários para a abertura de uma nova conta bancária")
public record ContaCreateDTO(

        @Schema(description = "Nome completo do titular da conta", example = "João da Silva")
        @NotBlank(message = "O nome do titular é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String titular,

        @Schema(description = "CPF do titular (apenas números ou com máscara)", example = "123.456.789-00")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "O CPF deve ter 11 dígitos ou estar no formato 000.000.000-00")
        String cpf
) {}