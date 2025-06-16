package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import com.example.demo.DTO.Relatorios.FuncionarioListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.FuncionarioDTO;
import com.example.demo.model.Funcionario;
import com.example.demo.model.Permissao;
import com.example.demo.service.FuncionarioService;
import com.example.demo.service.PermissaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/funcionario")
@AllArgsConstructor
public class FuncionarioController {

    @Autowired
    private final FuncionarioService funcionarioService;

    @Autowired
    private final PermissaoService permissaoService;

    @PostMapping("/criar")
    @Operation(description = "Cria um novo Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Funcionario seja criado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Funcionario criar(@RequestBody FuncionarioDTO funcionarioDTO) {
        return funcionarioService.saveAll(funcionarioDTO);
    }

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita um Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Funcionario seja editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Funcionario editar(@PathVariable UUID id, @RequestBody FuncionarioDTO funcionarioDTO)
            throws RelationTypeNotFoundException {
        return funcionarioService.editId(id, funcionarioDTO);
    }

    @GetMapping("/editar/test")
    @Operation(description = "Edita um Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Funcionario seja editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public FuncionarioDTO testar(@RequestBody FuncionarioDTO funcionarioDTO) {
        return funcionarioDTO;
    }

    @GetMapping("/listar")
    @Operation(description = "Lista todos os Funcionarios.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso os Funcionarios sejam listados com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<FuncionarioListDto> listarTodos() {
        return funcionarioService.findAllWithPermissao();
    }

    @GetMapping("/buscar/{id}")
    @Operation(description = "Busca um Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Funcionario seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public FuncionarioEditDto buscarPorId(@PathVariable UUID id) throws RelationTypeNotFoundException {
        return funcionarioService.findById(id);
    }

    @PutMapping("/mudarAtivo/{id}")
    @Operation(description = "Muda o status de ativo de um Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o status de ativo do Funcionario seja alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void mudarAtivo(@PathVariable UUID id) throws RelationTypeNotFoundException {
        funcionarioService.changeAtivo(id);
    }

    @PutMapping("/mudarSenha/{id}")
    @Operation(description = "Muda a senha de um Funcionario.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a senha do Funcionario seja alterada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void mudarSenha(@PathVariable UUID id, @RequestBody String senha) throws RelationTypeNotFoundException {
        funcionarioService.changePassword(id, senha);
    }

    @GetMapping("/permissoes/{id}")
    @Operation(description = "Retorna o Funcionario logado.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Funcionario logado seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Funcionario não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Permissao> me(@PathVariable UUID id) {
        Funcionario funcionario = funcionarioService.findById(id);
        List<Permissao> permissoes = permissaoService.findByFuncionario(funcionario.getId());
        return permissoes;
    }

}
