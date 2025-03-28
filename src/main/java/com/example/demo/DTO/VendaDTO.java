package com.example.demo.DTO;

import java.util.List;

public record VendaDTO(
        List<ProdutosDTO> produtos,
        String codigoCartao) {
}
