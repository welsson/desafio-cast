package com.cast4it.desafio.cast.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransacaoTest {

    @Test
    @DisplayName("Deve preencher dataHora no PrePersist quando estiver nulo")
    void devePreencherDataHoraNoPrePersist() {
        Transacao transacao = new Transacao();
        transacao.onCreate();
        assertNotNull(transacao.getDataHora());
    }

    @Test
    @DisplayName("Não deve sobrescrever dataHora no PrePersist se já estiver preenchido")
    void naoDeveSobrescreverDataHoraExistente() {
        LocalDateTime dataAntiga = LocalDateTime.now().minusDays(1);
        Transacao transacao = Transacao.builder()
                .dataHora(dataAntiga)
                .build();
        transacao.onCreate();
        assertEquals(dataAntiga, transacao.getDataHora());
    }
}