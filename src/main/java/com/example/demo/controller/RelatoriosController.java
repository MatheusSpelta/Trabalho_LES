package com.example.demo.controller;

import com.example.demo.DTO.Relatorios.*;
import com.example.demo.model.Cliente;
import com.example.demo.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorio")
@AllArgsConstructor
class RelatoriosController {

    private final RelatorioService relatoriosService;

    @GetMapping("/aniversariantes")
    @Operation(description = "Retorna os aniversariantes do mês atual.")
    public List<Cliente> aniversariantes() {
        return relatoriosService.clientesAniversariantesHoje();
    }

    @GetMapping("/relatorio-detalhado")
    @Operation(description = "Retorna um relatorio detalhado da ultima compra de cada cliente.")
    public List<ClienteRelatorioDetalhadoDTO> relatorioDetalhado() {
        return relatoriosService.obterRelatorioDetalhadoClientes();
    }

    @PostMapping("/ticket-medio")
    @Operation(description = "Retorna o ticket médio dos clientes entre duas datas.")
    public List<ClienteTicketMedioDTO> ticketMedio(@RequestBody DataRequestDTO request) {
        return relatoriosService.calcularTicketMedioPorCliente(request.getDataInicio(), request.getDataFim());
    }

    @PostMapping("/relatorio-diario")
    @Operation(description = "Retorna a lista de clientes atendidos por dia.")
    public List<ClienteConsumoDTO> relatorioDiario(@RequestBody LocalDate data) {
        return relatoriosService.consumoDiarioPorUsuario(data);
    }

    @GetMapping("/vendas-cliente/{clienteId}")
    @Operation(description = "Retorna as vendas de um cliente expecifico.")
    public ClienteRelatorioDTO vendasPorCliente(@PathVariable UUID clienteId) {
        return relatoriosService.obterVendasPorCliente(clienteId);
    }

    @PostMapping("/vendas-diaria")
    @Operation(description = "Retorna o total de vendas do dia.")
    public RelatorioTotalDTO vendasDiaria(@RequestBody LocalDate data) {
        return relatoriosService.obterVendasDiaria(data);
    }

    @PostMapping("/relatorio-produtos")
    @Operation(description = "Retorna o relatorio de produtos vendidos de um periodo expecifico")
    public List<ProdutoRelatorioDTO> relatorioProdutos(@RequestBody DataRequestDTO request) {
        return relatoriosService.obterRelatorioVendasPorProduto(request.getDataInicio(), request.getDataFim());
    }

    @PostMapping("/relatorio-compras")
    @Operation(description = "Retorna o relatorio de compras de um periodo expecifico")
    public RelatorioCompraDTO relatorioCompras(@RequestBody DataRequestDTO request) {
        return relatoriosService.obterRelatorioCompras(request.getDataInicio(), request.getDataFim());
    }

    @PostMapping("/dre")
    @Operation(description = "Retorna o DRE de um periodo expecifico")
    public DreDiarioDTO relatorioDRE(@RequestBody DataRequestDTO request) {
        System.out.println(request);
        return relatoriosService.gerarDreDiarioPorPeriodo(request.getDataInicio(), request.getDataFim());
    }
}

