package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.InterfacePermissao;
import com.example.demo.service.InterfacePermissaoService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/interfacePermissao")
@AllArgsConstructor
public class InterfacePermissaoController {

    @Autowired
    private final InterfacePermissaoService interfacePermissaoService;

    @GetMapping("/listar")
    @Operation(description = "Lista todas as interfaces de permissão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as interfaces de permissão sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<InterfacePermissao> listarTodos() {
        return interfacePermissaoService.findAll();
    }
}
