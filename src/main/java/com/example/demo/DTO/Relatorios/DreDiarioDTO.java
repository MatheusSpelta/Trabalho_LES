package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.util.List;

public record DreDiarioDTO(
        double totalRecebido,
        double totalDebito,
        double totalCredito,
        double totalPago,
        long registrosAPagar,
        double resultado,
        double saldoCaixa
) {
}
