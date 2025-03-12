package com.example.demo.DTO;

import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;

public record ClienteDTO(Cliente cliente, Endereco endereco) {
    
}
