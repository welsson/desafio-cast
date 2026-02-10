package com.cast4it.desafio.cast.dto;

import com.cast4it.desafio.cast.enums.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representação de transações no extrato.
 * Inclui dados da contraparte para garantir um extrato rico em detalhes.
 */
@Schema(description = "Representação detalhada de uma movimentação financeira no histórico")
public record TransacaoDTO(

        @Schema(description = "ID único da transação", example = "105")
        Long id,

        @Schema(description = "Valor da operação", example = "150.00")
        BigDecimal valor,

        @Schema(description = "Tipo da operação (CREDITO, DEBITO, TRANSFERENCIA_ENVIADA, etc.)", example = "TRANSFERENCIA_RECEBIDA")
        TipoTransacao tipo,

        @Schema(description = "Data e hora exata da operação", example = "2026-02-10T10:30:00")
        LocalDateTime dataHora,

        @Schema(description = "Número da conta que é dona deste extrato", example = "12345-6")
        String contaNumero,

        @Schema(description = "Nome do terceiro envolvido na transação (ex: quem enviou o Pix)", example = "Maria Oliveira")
        String contraparteNome,

        @Schema(description = "Número da conta do terceiro envolvido", example = "67890-1")
        String contraparteConta,

        @Schema(description = "Identificador único (UUID) que vincula o débito na origem ao crédito no destino", example = "550e8400-e29b-41d4-a716-446655440000")
        String codigoOperacao
) {}