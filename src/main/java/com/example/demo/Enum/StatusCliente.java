package com.example.demo.Enum;

public enum StatusCliente {
    LIBERADO(1),
    BLOQUEADO(2),
    PENDENTE(3);

    private final int code;

    StatusCliente(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StatusCliente valueOf(int code) {
        for (StatusCliente value : StatusCliente.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid StatusCliente code");
    }
}
