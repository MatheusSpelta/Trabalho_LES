package com.example.demo.controller;

import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Recarga;
import com.example.demo.service.RecargaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/recarga")
@AllArgsConstructor
public class RecargaController {
    
    @Autowired
    private final RecargaService recargaService;

    @PostMapping("/recarga")
    @Operation (description="Realiza uma nova recarga.", responses = {
        @ApiResponse(responseCode = "200", description = "Caso a recarga seja realizada com sucesso."),
        @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Recarga recarga(@RequestBody Recarga recarga) throws RelationTypeNotFoundException {
        return recargaService.saveAll(recarga);
    }

    @PutMapping("/editar/{id}")
    @Operation (description="Edita uma recarga.", responses = {
        @ApiResponse(responseCode = "200", description = "Caso a recarga seja editada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Recarga não encontrada."),
        @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Recarga editar(@PathVariable UUID id,@RequestBody Recarga recarga) throws RelationTypeNotFoundException {
        return recargaService.editId(id, recarga);
    }

    @PutMapping("/mudarAtivo/{id}")
    @Operation (description="Muda o status de ativo de uma recarga.", responses = {
        @ApiResponse(responseCode = "200", description = "Caso o status de ativo da recarga seja alterado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Recarga não encontrada."),
        @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void mudarAtivo(@PathVariable UUID id) throws RelationTypeNotFoundException {
        recargaService.desativarRecarga(id);
    }
}
