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

import com.example.demo.model.Produto;
import com.example.demo.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/produto")
@AllArgsConstructor
public class ProdutoController {

    @Autowired
    private final ProdutoService produtoService;

    @PostMapping("/criar")
    @Operation(description = "Cria um novo Produto.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Produto seja criado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Produto criar(@RequestBody Produto produto) {
        return produtoService.saveAll(produto);
    }

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita um Produto.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Produto seja editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Produto editar(@PathVariable UUID id, @RequestBody Produto produto) throws RelationTypeNotFoundException {
        return produtoService.editId(id, produto);
    }

    @GetMapping("/listar")
    @Operation(description = "Lista todos os Produtos.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso os Produtos sejam listados com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Produto> listarTodos() {
        return produtoService.findAll();
    }

    @GetMapping("/buscar/{id}")
    @Operation(description = "Busca um Produto pelo id.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Produto seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Produto buscarPorId(@PathVariable UUID id) throws RelationTypeNotFoundException {
        return produtoService.findById(id);
    }

    @GetMapping("/listar/ativos")
    @Operation(description = "Lista todos os Produtos ativos.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso os Produtos ativos sejam listados com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Produto> listarAtivos() {
        return produtoService.findAtivos();
    }

    @PutMapping("/mudarAtivo/{id}")
    @Operation(description = "Muda o status de ativo de um Produto.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o status de ativo do Produto seja alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void mudarAtivo(@PathVariable UUID id) throws RelationTypeNotFoundException {
        produtoService.changeAtivo(id);
    }


//    @PutMapping("/mudarPromocao/{id}")
//    @Operation(description = "Muda o status de promoção de um Produto.", responses = {
//            @ApiResponse(responseCode = "200", description = "Caso o status de promoção do Produto seja alterado com sucesso."),
//            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
//            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
//    })
//    public void mudarPromocao(@PathVariable UUID id) throws RelationTypeNotFoundException {
//        produtoService.changePromocao(id);
//    }

//    @PutMapping("/mudarKg/{id}")
//    @Operation(description = "Muda o status de kg de um Produto.", responses = {
//            @ApiResponse(responseCode = "200", description = "Caso o status de kg do Produto seja alterado com sucesso."),
//            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
//            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
//    })
//    public void mudarKg(@PathVariable UUID id) throws RelationTypeNotFoundException {
//        produtoService.changeKg(id);
//    }

    @GetMapping("/buscar/ean/{ean}")
    @Operation(description = "Busca um Produto pelo EAN.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o Produto seja encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Produto buscarPorEan(@PathVariable String ean) throws RelationTypeNotFoundException {
        return produtoService.findByEan(ean);
    }
}
