package com.example.demo.exception;

public class ProdutoException extends RuntimeException {

    public ProdutoException(String message) {
        super(message);
    }

    public ProdutoException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ProdutoException produtoNaoEncontrado() {
        return new ProdutoException("Produto não encontrado.");
    }

    public static ProdutoException codigoJaCadastrado() {
        return new ProdutoException("Já existe um produto com o código informado.");
    }

    public static ProdutoException eanJaCadastrado() {
        return new ProdutoException("Ean já cadastrado.");
    }
}
