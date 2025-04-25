package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

import java.util.List;

public record DreDiarioDTO(
        double totalRecebido,
        double totalGasto,
        Integer numClientesAtendidos,
        List<Cliente> clientesAtendidos
) {
}
