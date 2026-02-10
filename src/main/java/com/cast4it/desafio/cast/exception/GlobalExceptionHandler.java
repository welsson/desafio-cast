package com.cast4it.desafio.cast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<ApiError> handleContaNaoEncontrada(ContaNaoEncontradaException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "ContaNaoEncontrada",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ApiError> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "SaldoInsuficiente",
                "O saldo disponível na conta é insuficiente para realizar esta operação.",
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "DataIntegrityViolation",
                "Erro de integridade de dados (possível duplicidade ou violação de regra do banco).",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "InternalServerError",
                "Ocorreu um erro inesperado no servidor: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

        @ExceptionHandler(CpfNaoEncontradoException.class)
        public ResponseEntity<ApiError> handleCpfNaoEncontrado(CpfNaoEncontradoException ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND.value(),
                    "CpfNaoEncontrado",
                    ex.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(ValorInvalidoException.class)
        public ResponseEntity<ApiError> handleValorInvalido(ValorInvalidoException ex) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "ValorInvalido",
                    ex.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(ContasIdenticasException.class)
        public ResponseEntity<ApiError> handleContasIdenticas(ContasIdenticasException ex) {
            ApiError error = new ApiError(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    "ContasIdenticas",
                    ex.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        @ExceptionHandler(CpfJaCadastradoException.class)
        public ResponseEntity<ApiError> handleCpfJaCadastrado(CpfJaCadastradoException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT.value(),
                    "CpfJaCadastrado",
                    ex.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }


}