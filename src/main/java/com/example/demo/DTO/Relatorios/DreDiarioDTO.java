package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.time.LocalDate;
import java.util.List;

public record DreDiarioDTO(
        LocalDate data,
        double valorReceber,
        double valorPagar,
        double resultado,
        double saldo,
        Double saldoAnterior // pode ser null exceto no primeiro dia
) {
}
