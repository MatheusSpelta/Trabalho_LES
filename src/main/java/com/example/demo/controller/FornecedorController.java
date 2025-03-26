package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.FornecedorDTO;
import com.example.demo.model.Fornecedor;
import com.example.demo.service.FornecedorService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/fornecedor")
@AllArgsConstructor
public class FornecedorController {

    @Autowired
    private final FornecedorService fornecedorService;

    @PostMapping("/criar")
    @Operation(description = "Cria um novo Fornecedor.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Fornecedor seja criado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Fornecedor criar(@RequestBody FornecedorDTO fornecedorDTO) {
        return fornecedorService.saveAll(fornecedorDTO);
    }

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita um Fornecedor.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Fornecedor seja editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Fornecedor não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Fornecedor editar(@PathVariable UUID id, @RequestBody FornecedorDTO fornecedorDTO)
            throws RelationTypeNotFoundException {
        return fornecedorService.editId(id, fornecedorDTO);
    }

    @GetMapping("/listar")
    @Operation(description = "Lista todos os Fornecedores.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a lista de Fornecedores seja retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Fornecedor> listar() {
        return fornecedorService.findAll();
    }

    @GetMapping("/buscar/{id}")
    public Fornecedor buscar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        return fornecedorService.findById(id);
    }

    @PutMapping("/ativo/{id}")
    @Operation(description = "Ativa ou desativa um Fornecedor.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Fornecedor seja ativado ou desativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Fornecedor não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public ResponseEntity<?> ativar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        try {
            fornecedorService.changeAtivo(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}
