package com.example.demo.DTO;

import com.example.demo.model.Funcionario;
import com.example.demo.model.Permissao;

import java.util.List;

public record FuncionarioListDto(
        Funcionario funcionario,
        List<Permissao> permissao
) {
}
