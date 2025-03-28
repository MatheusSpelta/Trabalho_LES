package com.example.demo.exception;

public class VendaException extends RuntimeException {

    public VendaException(String message) {
        super(message);
    }

    public static VendaException saldoInsuficiente() {
        return new VendaException("Saldo do cartão insuficiente para realizar a venda.");
    }

    public static VendaException vendaNaoEncontrada() {
        return new VendaException("Venda não encontrado.");
    }

}
