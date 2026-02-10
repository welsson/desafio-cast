package com.cast4it.desafio.cast.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Define a natureza das movimentações financeiras no sistema.
 * Essencial para a categorização no extrato e lógica de cálculo de saldo.
 */
@Schema(description = "Categorias de movimentações financeiras permitidas")
public enum TipoTransacao {

    @Schema(description = "Entrada de valor via depósito (Soma ao saldo)")
    CREDITO,

    @Schema(description = "Saída de valor via saque (Subtrai do saldo)")
    DEBITO,

    @Schema(description = "Saída de valor via transferência para outra conta (Subtrai do saldo)")
    TRANSFERENCIA_ENVIADA,

    @Schema(description = "Entrada de valor via transferência recebida de outra conta (Soma ao saldo)")
    TRANSFERENCIA_RECEBIDA
}