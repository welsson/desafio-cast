package com.cast4it.desafio.cast.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContaTest {

    @Test
    @DisplayName("Deve executar lógica de PrePersist")
    void deveExecutarPrePersist() {
        Conta conta = new Conta();
        conta.onCreate();
        assertNotNull(conta.getCriadoEm());
        assertEquals(new BigDecimal("0.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Deve manter saldo existente no PrePersist")
    void deveManterSaldoNoPrePersist() {
        Conta conta = Conta.builder()
                .saldo(new BigDecimal("100.00"))
                .build();
        conta.onCreate();
        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Deve executar lógica de PreUpdate")
    void deveExecutarPreUpdate() {
        Conta conta = new Conta();
        conta.onUpdate();
        assertNotNull(conta.getAtualizadoEm());
    }
}