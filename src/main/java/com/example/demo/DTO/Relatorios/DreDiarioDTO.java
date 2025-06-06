package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.time.LocalDate;
import java.util.List;

public record DreDiarioDTO(
        List<DreDiarioDias> dreDias,
        double saldoAnterior
) {
    public record DreDiarioDias(
            LocalDate data,
            double valorReceber,
            double valorPagar,
            double resultado,
            double saldo,
            double saldoAnterior
    ) {

    }
}
