package com.example.demo.DTO.Relatorios;

import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.model.Cliente;

import java.util.List;

public record ClienteRelatorioDTO(
        Cliente cliente,
        List<VendaResponseDTO> vendas,
        double total,
        double totalCredito,
        double totalDebito
) {
}

