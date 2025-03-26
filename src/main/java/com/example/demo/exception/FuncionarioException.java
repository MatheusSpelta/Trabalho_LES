package com.example.demo.exception;

public class FuncionarioException extends RuntimeException {

    public FuncionarioException(String message) {
        super(message);
    }

    public FuncionarioException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FuncionarioException funcionarioNaoEncontrado() {
        return new FuncionarioException("Funcionário não encontrado.");
    }

    public static FuncionarioException cpfJaCadastrado() {
        return new FuncionarioException("Já existe um funcionário com o CPF informado.");
    }

    public static FuncionarioException matriculaJaCadastrada() {
        return new FuncionarioException("Já existe um funcionário com a matrícula informada.");
    }

    public static FuncionarioException emailJaCadastrado() {
        return new FuncionarioException("Já existe um funcionário com o email informado.");
    }
}
