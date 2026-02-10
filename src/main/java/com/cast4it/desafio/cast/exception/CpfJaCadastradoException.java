package com.cast4it.desafio.cast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção específica para tentativa de cadastro de CPF duplicado.
 * Retorna 409 Conflict, que é o status HTTP correto para quando
 * o recurso já existe e gera um conflito.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException(String cpf) {
        super(String.format("Já existe uma conta vinculada ao CPF: %s", cpf));
    }
}