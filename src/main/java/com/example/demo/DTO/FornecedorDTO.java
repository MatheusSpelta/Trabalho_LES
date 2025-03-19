package com.example.demo.DTO;

import com.example.demo.model.Endereco;
import com.example.demo.model.Fornecedor;

public record FornecedorDTO(
        Fornecedor fornecedor,
        Endereco endereco) {
}
