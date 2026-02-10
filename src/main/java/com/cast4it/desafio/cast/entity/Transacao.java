package com.cast4it.desafio.cast.entity;

import com.cast4it.desafio.cast.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que registra o histórico imutável de movimentações financeiras.
 * Mapeada para fins de auditoria e reconciliação bancária.
 */
@Entity
@Table(name = "transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoTransacao tipo;

    @Column(name = "contraparte_nome", length = 100)
    private String contraparteNome;

    @Column(name = "contraparte_conta", length = 20)
    private String contraparteConta;

    /**
     * Identificador único da operação (UUID) usado para vincular
     * os dois lados de uma transferência (débito e crédito).
     */
    @Column(name = "codigo_operacao", nullable = false, length = 36)
    private String codigoOperacao;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @PrePersist
    protected void onCreate() {
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
    }
}