package com.cast4it.desafio.cast.mapper;

import com.cast4it.desafio.cast.dto.TransacaoDTO;
import com.cast4it.desafio.cast.entity.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    /**
     * Converte a Entidade para DTO.
     * O campo 'contaNumero' vem do relacionamento Lazy com a entidade Conta.
     * Os demais campos de contraparte são mapeados automaticamente por possuírem nomes iguais.
     */
    @Mapping(source = "conta.numero", target = "contaNumero")
    @Mapping(source = "contraparteNome", target = "contraparteNome")
    @Mapping(source = "contraparteConta", target = "contraparteConta")
    @Mapping(source = "codigoOperacao", target = "codigoOperacao")
    TransacaoDTO toDTO(Transacao transacao);

    /**
     * Mapeamento inverso ignorando a entidade complexa 'Conta'
     * para evitar buscas desnecessárias ou estados inconsistentes.
     */
    @Mapping(target = "conta", ignore = true)
    Transacao toEntity(TransacaoDTO dto);
}