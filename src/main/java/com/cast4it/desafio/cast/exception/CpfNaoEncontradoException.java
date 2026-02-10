package com.cast4it.desafio.cast.exception;

public class CpfNaoEncontradoException extends RuntimeException {
    public CpfNaoEncontradoException(String cpf) {
        super("Conta com CPF " + cpf + " não encontrado");
    }
}
