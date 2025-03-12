package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.service.FuncionarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    
    @Autowired
    private final FuncionarioService funcionarioService;

    @PostMapping("/login")
    @Operation (description="Realiza o login do Funcionario.", responses = {
        @ApiResponse(responseCode = "200", description = "Caso o login seja realizado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Email ou senha invalidos."),
        @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return funcionarioService.login(loginRequest);
    }
}
