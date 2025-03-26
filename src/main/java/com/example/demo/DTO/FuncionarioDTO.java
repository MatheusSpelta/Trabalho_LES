package com.example.demo.DTO;

import java.util.List;

import com.example.demo.model.Endereco;
import com.example.demo.model.Funcionario;
import com.example.demo.model.Permissao;

public record FuncionarioDTO(Funcionario funcionario, Endereco endereco, List<Permissao> permissoes) {

}
