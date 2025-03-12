package com.example.demo.Enum;

public enum FormaPagamento {
    CREDITO(1),
    DEBITO(2);

    private final int code;

    private FormaPagamento(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FormaPagamento valueOf(int code) {
        for (FormaPagamento value : FormaPagamento.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de Forma de Pagamento inválido.");
    }
}
