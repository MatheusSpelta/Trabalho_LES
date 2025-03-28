package com.example.demo.exception;

public class UsuarioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioException(String message) {
        super(message);
    }

    public static UsuarioException usuarioNaoEncontrado() {
        return new UsuarioException("Usuário não encontrado.");
    }

    public static UsuarioException emailJaCadastrado() {
        return new UsuarioException("Email já cadastrado.");
    }

    public static UsuarioException senhaIncorreta() {
        return new UsuarioException("Senha incorreta.");
    }

}
