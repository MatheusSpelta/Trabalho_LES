package com.example.demo.DTO;

import com.example.demo.model.Funcionario;
import com.example.demo.model.Permissao;

import java.util.List;

public record FuncionarioEditDto(
        Funcionario funcionario,
        List<Permissao> permissaoList
) {
}
