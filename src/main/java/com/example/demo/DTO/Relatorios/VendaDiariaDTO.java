package com.example.demo.DTO.Relatorios;

import java.time.ZonedDateTime;

public record VendaDiariaDTO(
        String nomeCliente,
        double valorTotal,
        ZonedDateTime dataVenda
) {
}
