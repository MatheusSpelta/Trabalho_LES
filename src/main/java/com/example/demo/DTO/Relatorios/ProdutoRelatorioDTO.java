package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Produto;

public record ProdutoRelatorioDTO(
        Produto produto,
        double quantidadeVendida,
        double lucroTotal
) {
}
