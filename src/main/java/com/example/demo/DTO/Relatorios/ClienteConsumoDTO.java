package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

public record ClienteConsumoDTO(
        Cliente cliente,
        double valorConsumido
) {
}
