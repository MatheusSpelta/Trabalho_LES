package com.example.demo.service;

import com.example.demo.DTO.Relatorios.ClienteRelatorioDTO;
import com.example.demo.DTO.Relatorios.ClienteRelatorioDetalhadoDTO;
import com.example.demo.DTO.Relatorios.ClienteTicketMedioDTO;
import com.example.demo.DTO.Relatorios.RelatorioTotalDTO;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.model.Cliente;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RelatorioService {

    private final ClienteService clienteService;
    private final VendaService vendaService;

    public ClienteRelatorioDTO obterVendasPorCliente(UUID clienteId) {

        Cliente cliente = clienteService.findById(clienteId);
        List<VendaResponseDTO> vendas = vendaService.listarVendasPorClienteId(clienteId);

        double total = 0.0;
        double totalCredito = 0.0;
        double totalDebito = 0.0;

        for (VendaResponseDTO venda : vendas) {
            total += venda.venda().getValorTotal();
            totalCredito += venda.venda().getPagamentoCredito();
            totalDebito += venda.venda().getPagamentoDebito();
        }
        return new ClienteRelatorioDTO(cliente, vendas, total, totalCredito, totalDebito);
    }

    public RelatorioTotalDTO obterVendasDiaria(LocalDate data) {

        List<Cliente> clientes = clienteService.findAll();
        List<ClienteRelatorioDTO> clientesRelatorios = new ArrayList<>();

        double totalVenda = 0.0;
        double totalCredito = 0.0;
        double totalDebito = 0.0;

        for (Cliente cliente : clientes) {
            List<VendaResponseDTO> vendasFiltradas = vendaService.listarVendasPorClienteId(cliente.getId())
                    .stream()
                    .filter(venda -> venda.venda().getDataVenda().toLocalDate().equals(data))
                    .collect(Collectors.toList());

            double totalCliente = vendasFiltradas.stream().mapToDouble(v -> v.venda().getValorTotal()).sum();
            double totalCreditoCliente = vendasFiltradas.stream().mapToDouble(v -> v.venda().getPagamentoCredito()).sum();
            double totalDebitoCliente = vendasFiltradas.stream().mapToDouble(v -> v.venda().getPagamentoDebito()).sum();

            ClienteRelatorioDTO clienteRelatorio = new ClienteRelatorioDTO(cliente, vendasFiltradas, totalCliente, totalCreditoCliente, totalDebitoCliente);
            totalVenda += totalCliente;
            totalCredito += totalCreditoCliente;
            totalDebito += totalDebitoCliente;

            clientesRelatorios.add(clienteRelatorio);
        }

        return new RelatorioTotalDTO(clientesRelatorios, totalVenda, totalCredito, totalDebito);
    }

    public List<Cliente> clientesAtendidosPorDia(LocalDate data) {
        return vendaService.findClientesAtendidos(data);
    }

    public List<Cliente> clientesAniversariantesHoje() {
        return clienteService.findClientesAniversariantesHoje();
    }

    public List<ClienteTicketMedioDTO> calcularTicketMedioPorCliente(LocalDate dataInicio, LocalDate dataFim) {
        List<Cliente> clientes = clienteService.findAll();
        List<ClienteTicketMedioDTO> ticketMedioPorCliente = new ArrayList<>();

        for (Cliente cliente : clientes) {
            List<VendaResponseDTO> vendasFiltradas = vendaService.listarVendasPorClienteId(cliente.getId())
                    .stream()
                    .filter(venda -> {
                        LocalDate dataVenda = venda.venda().getDataVenda().toLocalDate();
                        return (dataVenda.isEqual(dataInicio) || dataVenda.isAfter(dataInicio)) &&
                                (dataVenda.isEqual(dataFim) || dataVenda.isBefore(dataFim));
                    })
                    .toList();

            double totalVendas = vendasFiltradas.stream().mapToDouble(v -> v.venda().getValorTotal()).sum();
            int quantidadeVendas = vendasFiltradas.size();
            double ticketMedio = quantidadeVendas > 0 ? totalVendas / quantidadeVendas : 0.0;

            ticketMedioPorCliente.add(new ClienteTicketMedioDTO(cliente, ticketMedio));
        }

        return ticketMedioPorCliente;
    }

    public List<ClienteRelatorioDetalhadoDTO> obterRelatorioDetalhadoClientes() {
        List<Cliente> clientes = clienteService.findAll();
        List<ClienteRelatorioDetalhadoDTO> relatorio = new ArrayList<>();

        for (Cliente cliente : clientes) {
            List<VendaResponseDTO> vendas = vendaService.listarVendasPorClienteId(cliente.getId());

            double valorVendido = vendas.stream()
                    .mapToDouble(v -> v.venda().getValorTotal())
                    .sum();

            LocalDate dataUltimaCompra = vendas.stream()
                    .map(v -> v.venda().getDataVenda().toLocalDate())
                    .max(LocalDate::compareTo)
                    .orElse(null);

            relatorio.add(new ClienteRelatorioDetalhadoDTO(
                    cliente,
                    valorVendido,
                    cliente.getLimiteCredito(),
                    cliente.getSaldoDebito(),
                    dataUltimaCompra
            ));
        }

        return relatorio;
    }
}
