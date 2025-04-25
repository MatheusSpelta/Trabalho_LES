package com.example.demo.service;

import com.example.demo.DTO.Relatorios.*;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RelatorioService {

    private final ClienteService clienteService;
    private final VendaService vendaService;
    private final VendaProdutoService vendaProdutoService;
    private final CompraService compraService;

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

    public List<ProdutoRelatorioDTO> obterRelatorioVendasPorProduto(LocalDate dataInicio, LocalDate dataFim) {
        // Buscar todas as vendas no período
        List<Venda> vendas = vendaService.listAll().stream()
                .filter(venda -> {
                    LocalDate dataVenda = venda.getDataVenda().toLocalDate();
                    return (dataVenda.isEqual(dataInicio) || dataVenda.isAfter(dataInicio)) &&
                            (dataVenda.isEqual(dataFim) || dataVenda.isBefore(dataFim));
                })
                .toList();

        Map<UUID, ProdutoRelatorioDTO> relatorioMap = new HashMap<>();

        // Iterar sobre as vendas e buscar os produtos vendidos
        for (Venda venda : vendas) {
            List<VendaProduto> produtosVendidos = vendaProdutoService.findByVendaId(venda.getId());

            for (VendaProduto vendaProduto : produtosVendidos) {
                Produto produto = vendaProduto.getProduto();
                double lucroUnitario = produto.getValorVenda() - produto.getValorCusto();
                double lucroTotal = lucroUnitario * vendaProduto.getQuantidade();

                relatorioMap.compute(produto.getId(), (id, relatorio) -> {
                    if (relatorio == null) {
                        return new ProdutoRelatorioDTO(
                                produto,
                                vendaProduto.getQuantidade(),
                                lucroTotal
                        );
                    } else {
                        return new ProdutoRelatorioDTO(
                                produto,
                                relatorio.quantidadeVendida() + vendaProduto.getQuantidade(),
                                relatorio.lucroTotal() + lucroTotal
                        );
                    }
                });
            }
        }

        // Ordenar os produtos por descrição
        return relatorioMap.values().stream()
                .sorted(Comparator.comparing(r -> r.produto().getDescricao()))
                .toList();
    }

    public RelatorioCompraDTO obterRelatorioCompras(LocalDate dataInicio, LocalDate dataFim) {
        List<Compra> compras = compraService.findAll();
        double totalApagar = 0;
        double totalPago = 0;
        double totalVencido = 0;

        List<Compra> comprasApagar = new ArrayList<>();
        List<Compra> comprasPagas = new ArrayList<>();
        List<Compra> comprasVencidas = new ArrayList<>();

        for (Compra compra : compras) {
            if (compra.isPago()) {
                totalPago += compra.getValorTotal();
                comprasPagas.add(compra);
            } else {
                totalApagar += compra.getValorTotal();
                comprasApagar.add(compra);

                if (compra.getDataVencimento() != null && compra.getDataVencimento().toLocalDate().isBefore(LocalDate.now())) {
                    totalVencido += compra.getValorTotal();
                    comprasVencidas.add(compra);
                }
            }
        }

        return new RelatorioCompraDTO(comprasApagar, comprasPagas, comprasVencidas, totalApagar, totalPago, totalVencido);
    }

    public DreDiarioDTO gerarDreDiario(LocalDate dataInicio, LocalDate dataFim) {
        List<Cliente> clientesAtendidos = clienteService.findAll().stream()
                .filter(cliente -> vendaService.listarVendasPorClienteId(cliente.getId()).stream()
                        .anyMatch(venda -> {
                            LocalDate dataPagamento = venda.venda().getDataPagamento() != null
                                    ? venda.venda().getDataPagamento().toLocalDate()
                                    : null;
                            return dataPagamento != null &&
                                    (dataPagamento.isEqual(dataInicio) || dataPagamento.isAfter(dataInicio)) &&
                                    (dataPagamento.isEqual(dataFim) || dataPagamento.isBefore(dataFim));
                        }))
                .toList();

        double totalRecebido = clientesAtendidos.stream()
                .flatMap(cliente -> vendaService.listarVendasPorClienteId(cliente.getId()).stream())
                .filter(venda -> venda.venda().getDataPagamento() != null &&
                        !venda.venda().getDataPagamento().toLocalDate().isBefore(dataInicio) &&
                        !venda.venda().getDataPagamento().toLocalDate().isAfter(dataFim))
                .mapToDouble(venda -> venda.venda().getValorTotal())
                .sum();

        double totalGasto = compraService.findAll().stream()
                .filter(compra -> compra.getDataPagamento() != null &&
                        !compra.getDataPagamento().toLocalDate().isBefore(dataInicio) &&
                        !compra.getDataPagamento().toLocalDate().isAfter(dataFim))
                .mapToDouble(Compra::getValorTotal)
                .sum();

        // Retornar o DRE Diário
        return new DreDiarioDTO(totalRecebido, totalGasto, clientesAtendidos.size(), clientesAtendidos);
    }

}
