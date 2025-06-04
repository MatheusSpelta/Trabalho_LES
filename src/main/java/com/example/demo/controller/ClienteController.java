package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.ClienteDTO;
import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/cliente")
@AllArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/criar")
    @Operation(description = "Cria um novo Cliente.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja criado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Cliente criar(@RequestBody ClienteDTO clienteDTO) {
        return clienteService.saveAll(clienteDTO);
    }

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita um Cliente.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Cliente editar(@PathVariable UUID id, @RequestBody ClienteDTO clienteDTO) throws RelationTypeNotFoundException {
        return clienteService.editId(id, clienteDTO);
    }

    @GetMapping("/listar")
    @Operation(description = "Lista todos os Clientes.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a lista de Clientes seja retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Cliente> listar() {
        return clienteService.findAll();
    }

    @GetMapping("/buscar/{id}")
    @Operation(description = "Busca um Cliente pelo ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Cliente buscar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        return clienteService.findById(id);
    }

    @PutMapping("/ativar/{id}")
    @Operation(description = "Ativa um Cliente.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja ativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void ativar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        clienteService.changeAtivo(id);
    }

    @GetMapping("/buscar/cartao/{cartao}")
    @Operation(description = "Busca um Cliente pelo Cartão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Cliente buscarPorCartao(@PathVariable String cartao) throws RelationTypeNotFoundException {
        return clienteService.findByCartao(cartao);
    }

}
