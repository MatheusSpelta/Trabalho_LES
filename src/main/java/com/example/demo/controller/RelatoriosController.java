package com.example.demo.controller;

import com.example.demo.DTO.Relatorios.ClienteRelatorioDetalhadoDTO;
import com.example.demo.DTO.Relatorios.ClienteTicketMedioDTO;
import com.example.demo.DTO.Relatorios.DataRequestDTO;
import com.example.demo.model.Cliente;
import com.example.demo.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/relatorio-diario")
    @Operation(description = "Retorna a lista de clientes atendidos por dia.")
    public List<Cliente> relatorioDiario(@RequestBody LocalDate data) {
        return relatoriosService.clientesAtendidosPorDia(data);
    }
}
