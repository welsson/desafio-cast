package com.cast4it.desafio.cast.repository;

import com.cast4it.desafio.cast.entity.Conta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    /**
     * Recupera uma conta aplicando Lock Pessimista de Escrita (SELECT FOR UPDATE).
     * Utilizado para garantir a consistência do saldo durante operações críticas,
     * prevenindo o fenômeno de "Lost Update" em cenários de alta concorrência.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.id = :id")
    Optional<Conta> findByIdForUpdate(@Param("id") Long id);

    Optional<Conta> findByNumero(String numero);

    Optional<Conta> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByNumero(String numero);
}