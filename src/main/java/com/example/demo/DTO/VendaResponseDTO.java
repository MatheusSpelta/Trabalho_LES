package com.example.demo.DTO;

import java.util.List;

import com.example.demo.model.Venda;
import com.example.demo.model.VendaProduto;

public record VendaResponseDTO(
        Venda venda,
        List<VendaProduto> produtos) {

}
