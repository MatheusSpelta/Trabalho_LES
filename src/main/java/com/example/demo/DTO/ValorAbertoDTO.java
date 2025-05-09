package com.example.demo.DTO;

import com.example.demo.model.Cliente;
import com.example.demo.model.Venda;

import java.util.List;

public record ValorAbertoDTO(
        Cliente cliente,
        List<Venda> vendas,
        float valorTotal
) {
}
