package com.cast4it.desafio.cast.service;

import com.cast4it.desafio.cast.entity.Conta;
import com.cast4it.desafio.cast.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Teste de Concorrência - Bloqueio Pessimista")
class ContaServiceConcurrencyTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Deve garantir integridade do saldo com 10 depósitos simultâneos")
    void deveGarantirIntegridadeComMultiplasThreads() throws InterruptedException {
        // Criar uma conta
        Conta conta = Conta.builder()
                .titular("Teste Concorrente")
                .cpf("00000000000")
                .numero("99999-9")
                .saldo(BigDecimal.ZERO)
                .build();
        conta = contaRepository.save(conta);
        Long contaId = conta.getId();

        int numeroDeThreads = 10;
        BigDecimal valorDeposito = new BigDecimal("100.00");

        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numeroDeThreads);

        // 10 threads simultâneas
        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    contaService.creditar(contaId, valorDeposito);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown();
        doneLatch.await();
        executor.shutdown();

        // saldo final DEVE ser 1000.00 (10 * 100)
        Conta contaFinal = contaRepository.findById(contaId).orElseThrow();

        // bloqueio pessimista falhar, o saldo será menor que 1000
        assertEquals(new BigDecimal("1000.00").setScale(2), contaFinal.getSaldo().setScale(2));
        System.out.println("Saldo final após concorrência: " + contaFinal.getSaldo());
    }
}