package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.VendaDTO;
import com.example.demo.model.Venda;
import com.example.demo.service.VendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/venda")
public class VendaController {

    @Autowired
    private final VendaService vendaService;

    @PostMapping("/criar")
    @Operation(description = "Cria uma nova venda.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a venda seja criada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Venda criarVenda(@RequestBody VendaDTO vendaDTO) {
        return vendaService.realizarVenda(vendaDTO);
    }
}
