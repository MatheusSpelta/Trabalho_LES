package com.example.demo.controller;

import com.example.demo.DTO.Relatorios.ClienteId;
import com.example.demo.DTO.Relatorios.DataRequestDTO;
import com.example.demo.DTO.ValorAbertoDTO;
import com.example.demo.DTO.VendaDTO;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.model.Cliente;
import com.example.demo.model.Venda;
import com.example.demo.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        return vendaService.listarTodasVendasPorCartaoCliente(cartao);
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

    @GetMapping("/imprimir/{vendaId}")
    @Operation(description = "Imprime um comprovante de venda novamente.")
    public ResponseEntity<Void> reimprimirVenda(@PathVariable UUID vendaId) {
        vendaService.reimprimirVenda(vendaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("imprimir/ultimaVenda/{cartaoId}")
    @Operation(description = "Imprime a ultima venda do cliente baseado o cartao.")
    public ResponseEntity<?> imprimirUltimaVenda(@PathVariable String cartaoId) {
        vendaService.imprimirUltimaVenda(cartaoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/emAberto")
    @Operation(description = "Retorna uma lsitagem contendo todas as vendas em aberto de cada Cliente com seus valores totais.")
    public ResponseEntity<List<ValorAbertoDTO>> listarVendasEmAberto(@RequestBody DataRequestDTO data) {
        List<ValorAbertoDTO> vendasEmAberto = vendaService.listarVendasEmAberto(data);
        return ResponseEntity.ok(vendasEmAberto);
    }

    @PostMapping("vendas/pagar-vendas")
    @Operation(description = "Atualiza todas as vendas em aberto do cliente para paga e zera o saldo em credito em aberto do cliente.")
    public ResponseEntity<Cliente> pagarVendas(@RequestBody ClienteId cliente) {
        return ResponseEntity.ok(vendaService.quitarDebito(cliente.clienteId()));

    }

    @PostMapping("/ativo/{id}")
    @Operation(description = "Ativa ou desativa uma venda.", responses = {
            @ApiResponse(responseCode = "200", description = "Caso a venda seja ativada/desativada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Venda não encontrada."),
            @ApiResponse(responseCode = "500", description = "Caso não tenha sido possível realizar a operação.")
    })
    public ResponseEntity<?> ativarDesativarVenda(@PathVariable UUID id) {
        try {
            vendaService.excluirVenda(id);
            return ResponseEntity.ok().body("Status da venda alterado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
