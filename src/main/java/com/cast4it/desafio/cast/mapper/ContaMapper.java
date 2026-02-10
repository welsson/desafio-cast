package com.cast4it.desafio.cast.mapper;

import com.cast4it.desafio.cast.dto.ContaDTO;
import com.cast4it.desafio.cast.entity.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContaMapper {


    ContaDTO toDTO(Conta conta);

    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "transacoes", ignore = true)
    Conta toEntity(ContaDTO dto);
}