package com.cast4it.desafio.cast.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long id) {
        super("Conta com ID " + id + " não encontrada");
    }
}
