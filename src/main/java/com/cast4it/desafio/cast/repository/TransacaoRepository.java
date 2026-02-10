package com.cast4it.desafio.cast.repository;

import com.cast4it.desafio.cast.entity.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    /**
     * Busca o histórico completo sem paginação.
     * @deprecated Utilizar a versão paginada para evitar problemas de memória em contas com alto volume.
     */
    @Deprecated
    List<Transacao> findByContaIdOrderByDataHoraDesc(Long contaId);

    /**
     * Recupera transações paginadas e ordenadas pela data mais recente.
     * O retorno 'Page' é essencial para que o Mat-Paginator no Angular
     * saiba o total de registros sem precisar de uma segunda chamada.
     */
    Page<Transacao> findByContaIdOrderByDataHoraDesc(Long contaId, Pageable pageable);

    Page<Transacao> findByContaId(Long contaId, Pageable pageable);
}