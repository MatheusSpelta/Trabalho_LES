package com.example.demo.DTO;

import com.example.demo.model.Produto;

public record ProdutosDTO(
                Produto produto,
                double quantidade,
                double valorTotal) {
}
