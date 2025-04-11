package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.VendaDTO;
import com.example.demo.DTO.VendaResponseDTO;
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

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita uma venda.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a venda seja editada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Venda não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Venda editarVenda(@PathVariable UUID id, @RequestBody VendaDTO vendaDTO) {
        return vendaService.editarVenda(id, vendaDTO);
    }

    @GetMapping("/listar/vendasAtivas/clienteCartao/{cartao}")
    @Operation(description = "Lista todas as vendas ATIVAS de um cliente baseados no cartão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as vendas sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<VendaResponseDTO> listarVendasPorCliente(@PathVariable String cartao) {
        return vendaService.listarVendasPorCartaoCliente(cartao, true);
    }

    @GetMapping("/listar/todas/vendas/clienteCartao/{cartao}")
    @Operation(description = "Lista todas as vendas de um cliente baseadas no cartão.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as vendas sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<VendaResponseDTO> listarTodasVendasPorCliente(@PathVariable String cartao) {
        return vendaService.listarTodasVendasPorCartaoCliente(cartao);
    }

    @GetMapping("/listar/vendas/cliente/{id}")
    @Operation(description = "Lista todas as vendas de um cliente baseadas no ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as vendas sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<VendaResponseDTO> listarVendasPorClienteId(@PathVariable UUID id) {
        return vendaService.listarVendasPorClienteId(id);
    }

    @GetMapping("/buscar/{id}")
    @Operation(description = "Retorna uma venda e seus produtos com base no ID da venda.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a venda seja encontrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Venda não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public VendaResponseDTO buscarVendaPorId(@PathVariable UUID id) {
        return vendaService.findVendaById(id);
    }

    @GetMapping("/listar/todas")
    @Operation(description = "Lista todas as vendas.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as vendas sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Venda> listarTodasVendas() {
        return vendaService.listAll();
    }
}
