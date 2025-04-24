package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.time.LocalDate;

public record ClienteRelatorioDetalhadoDTO(
        Cliente cliente,
        double valorVendido,
        double saldoCredito,
        double saldoDebito,
        LocalDate dataUltimaCompra
) {
}