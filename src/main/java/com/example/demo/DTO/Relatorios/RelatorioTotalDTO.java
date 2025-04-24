package com.example.demo.DTO.Relatorios;

import java.util.List;

public record RelatorioTotalDTO(
        List<ClienteRelatorioDTO> clientes,
        double totalVenda,
        double totalCredito,
        double totalDebito
) {
}
