package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Permissao;
import com.example.demo.service.PermissaoService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/permissao")
@AllArgsConstructor
public class PermissaoController {

    @Autowired
    private final PermissaoService permissaoService;

    @GetMapping("/listar/funcionario/{id}")
    @Operation(description = "Lista todas as permissões de um funcionário", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as permissões sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Permissao> listar(@PathVariable UUID id) {
        return permissaoService.findByFuncionario(id);
    }

    @PutMapping("/editar")
    @Operation(description = "Edita uma permissão", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a permissão seja editada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Permissão não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Permissao> permissoes(@RequestBody List<Permissao> permissoes) {
        return permissaoService.editar(permissoes);
    }
}
