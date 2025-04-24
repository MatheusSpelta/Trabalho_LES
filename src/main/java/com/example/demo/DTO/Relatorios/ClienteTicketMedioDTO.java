package com.example.demo.DTO.Relatorios;

import com.example.demo.model.Cliente;

public record ClienteTicketMedioDTO(
        Cliente cliente,
        double ticketMedio
) {
}
