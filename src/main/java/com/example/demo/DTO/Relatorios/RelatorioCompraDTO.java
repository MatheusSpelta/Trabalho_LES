package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Compra;

import java.util.List;

public record RelatorioCompraDTO(
        List<Compra> comprasApagar,
        List<Compra> comprasPagas,
        List<Compra> comprasVencidas,
        double totalApagar,
        double totalPago,
        double totalVencido
) {
}
