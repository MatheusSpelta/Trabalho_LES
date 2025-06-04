package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.time.LocalDate;

public record ClienteConsumoDTO(
        Cliente cliente,
        double valorConsumido,
        double totalGeral,
        LocalDate data

) {
}
