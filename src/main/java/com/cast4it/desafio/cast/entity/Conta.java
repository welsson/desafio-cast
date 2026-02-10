package com.cast4it.desafio.cast.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a conta bancária no domínio do sistema.
 * Implementa Optimistic Locking através da anotação @Version para garantir a
 * integridade do saldo em transações concorrentes.
 */
@Entity
@Table(name = "contas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String numero;

    @Column(nullable = false, length = 100)
    private String titular;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    /**
     * Controle de concorrência otimista.
     * Impede que duas threads sobrescrevam o saldo simultaneamente sem conhecimento uma da outra.
     */
    @Version
    private Long versao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transacao> transacoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        if (this.saldo == null) {
            this.saldo = BigDecimal.ZERO.setScale(2);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}