package com.example.demo.DTO;

import java.util.List;

import com.example.demo.model.Funcionario;
import com.example.demo.model.Permissao;

public record LoginResponse(
        String token,
        Funcionario funcionario,
        List<Permissao> permissoes) {

}
