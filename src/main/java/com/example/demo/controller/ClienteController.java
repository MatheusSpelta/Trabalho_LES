package com.example.demo.controller;

import com.example.demo.DTO.ClienteDTO;
import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationTypeNotFoundException;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/excluir/{id}")
    @Operation(description = "Ativa um Cliente.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja ativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public ResponseEntity<?> ativar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        clienteService.changeAtivo(id);
        return ResponseEntity.ok().body("Status do cliente alterado com sucesso!");
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

    @GetMapping("/buscar/cartaoo/{cartao}")
    @Operation(description = "Busca um Cliente pelo Cartão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Cliente seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Cliente entrarNoRefeitorio(@PathVariable String cartao) throws RelationTypeNotFoundException {
        return clienteService.entrarRefeitorio(cartao);
    }

}
