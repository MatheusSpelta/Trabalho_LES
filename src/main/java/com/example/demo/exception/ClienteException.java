package com.example.demo.exception;

public class ClienteException extends RuntimeException {

    public ClienteException(String message) {
        super(message);
    }

    public ClienteException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ClienteException clienteNaoEncontrado() {
        return new ClienteException("Cliente não encontrado.");
    }

    public static ClienteException matriculaJaCadastrada() {
        return new ClienteException("Já existe um cliente com a matrícula informada.");
    }

    public static ClienteException cpfJaCadastrado() {
        return new ClienteException("Já existe um cliente com o CPF informado.");
    }

    public static ClienteException cartaoJaCadastrado() {
        return new ClienteException("Já existe um cliente com o cartão informado.");
    }

    public static ClienteException saldoInsuficiente() {
        return new ClienteException("Saldo insuficiente.");
    }

    public static ClienteException clienteInativo() {
        return new ClienteException("Cliente inativo.");
    }

    public static ClienteException cartaoNaoEncontrado() {
        return new ClienteException("Cartão não encontrado.");
    }

}
