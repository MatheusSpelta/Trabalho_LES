package com.example.demo.controller;

import com.example.demo.model.Compra;
import com.example.demo.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationTypeNotFoundException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compra")
@AllArgsConstructor
public class CompraController {

    @Autowired
    private final CompraService compraService;

    @PostMapping("/criar")
    @Operation(description = "Cria uma nova compra", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a compra seja criada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Compra criar(@RequestBody Compra compra) {
        return compraService.saveAll(compra);
    }

    @PutMapping("/editar/{id}")
    @Operation(description = "Edita uma compra", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a compra seja editada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Compra não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Compra editar(@PathVariable UUID id, @RequestBody Compra compra) throws RelationTypeNotFoundException {
        return compraService.editId(id, compra);
    }

    @GetMapping("/listar")
    @Operation(description = "Lista todas as compras", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as compras sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Compra> listar() {
        return compraService.findAll();
    }

    @GetMapping("/listar/{id}")
    @Operation(description = "Lista uma compra", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a compra seja listada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Compra não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public Compra listar(@PathVariable UUID id) throws RelationTypeNotFoundException {
        return compraService.findById(id);
    }

    @PostMapping("/ativo/{id}")
    @Operation(description = "Altera o status de ativo de uma compra", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o status de ativo da compra seja alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Compra não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public ResponseEntity<?> mudarAtivo(@PathVariable UUID id) throws RelationTypeNotFoundException {
        compraService.changeAtivo(id);
        return ResponseEntity.ok().body("Status da compra alterado com sucesso!");
    }

    @PutMapping("/pago/{id}")
    @Operation(description = "Altera o status de pago de uma compra", responses = {
            @ApiResponse(responseCode = "200", description = "Caso o status de pago da compra seja alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Compra não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public void mudarPago(@PathVariable UUID id) throws RelationTypeNotFoundException {
        compraService.changeIsPago(id);
    }

    @GetMapping("/listar/vencidas")
    @Operation(description = "Lista todas as compras vencidas", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as compras vencidas sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Compra> listarVencidas() {
        return compraService.findComprasVencidas();
    }

    @GetMapping("/listar/fornecedor/{fornecedorId}")
    @Operation(description = "Lista todas as compras de um fornecedor", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as compras do fornecedor sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Compra> listarPorFornecedor(@PathVariable UUID fornecedorId) {
        return compraService.findComprasByFornecedor(fornecedorId);
    }

    @GetMapping("/listar/vencidas/fornecedor/{fornecedorId}")
    @Operation(description = "Lista todas as compras vencidas de um fornecedor", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as compras vencidas do fornecedor sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Compra> listarVencidasPorFornecedor(@PathVariable UUID fornecedorId) {
        return compraService.findComprasVencidasByFornecedor(fornecedorId);
    }

    @GetMapping("/listar/pagas/fornecedor/{fornecedorId}")
    @Operation(description = "Lista todas as compras pagas de um fornecedor", responses = {
            @ApiResponse(responseCode = "200", description = "Caso as compras pagas do fornecedor sejam listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public List<Compra> listarPagasPorFornecedor(@PathVariable UUID fornecedorId) {
        return compraService.findComprasPagasByFornecedor(fornecedorId);
    }

}
