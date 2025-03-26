package com.example.demo.exception;

public class FornecedorException extends RuntimeException{
    
    public FornecedorException(String message) {
        super(message);
    }

    public FornecedorException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FornecedorException fornecedorNaoEncontrado() {
        return new FornecedorException("Fornecedor não encontrado.");
    }

    public static FornecedorException cnpjJaCadastrado() {
        return new FornecedorException("Já existe um fornecedor com o CNPJ informado.");
    }

    
}
