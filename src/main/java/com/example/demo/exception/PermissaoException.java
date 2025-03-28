package com.example.demo.exception;

public class PermissaoException extends RuntimeException {

    public PermissaoException(String message) {
        super(message);
    }

    public static PermissaoException permissaoNaoEncotnrada() {
        return new PermissaoException("Permissao não encontrado.");
    }

}
