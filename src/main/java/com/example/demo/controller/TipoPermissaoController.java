package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.TipoPermissao;
import com.example.demo.service.TipoPermissaoService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/tipoPermissao")
@AllArgsConstructor
public class TipoPermissaoController {

    @Autowired
    private final TipoPermissaoService tipoPermissaoService;

    @GetMapping("/listar")
    @Operation(description = "Lista todos os tipos de permissão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso os tipos de permissão sejam listados com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<TipoPermissao> listarTodos() {
        return tipoPermissaoService.findAll();
    }
}
