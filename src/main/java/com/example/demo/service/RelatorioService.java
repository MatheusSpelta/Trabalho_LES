package com.example.demo.service;

import com.example.demo.DTO.Relatorios.*;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
        LocalDate inicio = data;
        LocalDate fim = data;

        List<Cliente> clientes = clienteService.findAll();
        List<ClienteRelatorioDTO> clientesRelatorios = new ArrayList<>();

        double totalVenda = 0.0;
        double totalCredito = 0.0;
        double totalDebito = 0.0;

        for (Cliente cliente : clientes) {
            List<VendaResponseDTO> vendasFiltradas = vendaService.listarVendasPorClienteId(cliente.getId())
                    .stream()
                    .filter(venda -> {
                        ZonedDateTime dataVendaZoned = venda.venda().getDataCriacao();
                        if (dataVendaZoned == null) return false;
                        LocalDate dataVenda = dataVendaZoned.toLocalDate();
                        return (dataVenda.isEqual(inicio) || dataVenda.isAfter(inicio)) &&
                                (dataVenda.isEqual(fim) || dataVenda.isBefore(fim));
                    })
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

    public List<ClienteConsumoDTO> consumoDiarioPorUsuario(LocalDate data) {
        List<Cliente> clientes = clienteService.findAll();
        double totalGeral = 0;
        List<ClienteConsumoDTO> lista = new ArrayList<>();
        for (Cliente cliente : clientes) {
            double valor = vendaService.listarVendasPorClienteId(cliente.getId()).stream()
                    .filter(v -> v.venda().getDataCriacao().toLocalDate().isEqual(data))
                    .mapToDouble(v -> v.venda().getValorTotal()).sum();
            totalGeral += valor;
            lista.add(new ClienteConsumoDTO(cliente, valor, 0, data)); // totalGeral será preenchido depois
        }
        for (ClienteConsumoDTO dto : lista) {
            lista.set(lista.indexOf(dto), new ClienteConsumoDTO(dto.cliente(), dto.valorConsumido(), totalGeral, dto.data()));
        }
        return lista;
    }

    public List<Cliente> clientesAniversariantesHoje() {
        return clienteService.findClientesAniversariantesHoje();
    }

    public List<ClienteTicketMedioDTO> calcularTicketMedioPorCliente(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate inicio = dataInicio;
        LocalDate fim = dataFim;

        List<Cliente> clientes = clienteService.findAll();
        List<ClienteTicketMedioDTO> ticketMedioPorCliente = new ArrayList<>();

        for (Cliente cliente : clientes) {
            List<VendaResponseDTO> vendasFiltradas = vendaService.listarVendasPorClienteId(cliente.getId())
                    .stream()
                    .filter(venda -> {
                        LocalDate dataVenda = venda.venda().getDataCriacao().toLocalDate();
                        return (dataVenda.isEqual(inicio) || dataVenda.isAfter(inicio)) &&
                                (dataVenda.isEqual(fim) || dataVenda.isBefore(fim));
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
            double valorVendido = vendas.stream().mapToDouble(v -> v.venda().getValorTotal()).sum();
            LocalDate dataUltimaCompra = vendas.stream()
                    .map(v -> v.venda().getDataCriacao().toLocalDate())
                    .max(LocalDate::compareTo).orElse(null);
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
        LocalDate inicio = dataInicio;
        LocalDate fim = dataFim;

        List<Venda> vendas = vendaService.listAll().stream()
                .filter(venda -> {
                    LocalDate dataVenda = venda.getDataCriacao().toLocalDate();
                    return (dataVenda.isEqual(inicio) || dataVenda.isAfter(inicio)) &&
                            (dataVenda.isEqual(fim) || dataVenda.isBefore(fim));
                })
                .toList();

        Map<UUID, ProdutoRelatorioDTO> relatorioMap = new HashMap<>();

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

        return relatorioMap.values().stream()
                .sorted(Comparator.comparing(r -> r.produto().getDescricao()))
                .toList();
    }

    public RelatorioCompraDTO obterRelatorioCompras(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate inicio = dataInicio;
        LocalDate fim = dataFim;

        List<Compra> compras = compraService.findAll().stream()
                .filter(compra -> {
                    LocalDate dataCompra = compra.getDataCompra().toLocalDate();
                    return (dataCompra.isEqual(inicio) || dataCompra.isAfter(inicio)) &&
                            (dataCompra.isEqual(fim) || dataCompra.isBefore(fim));
                })
                .toList();

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

                if (compra.getDataVencimento() != null && compra.getDataVencimento().isBefore(LocalDateTime.now())) {
                    totalVencido += compra.getValorTotal();
                    comprasVencidas.add(compra);
                }
            }
        }

        return new RelatorioCompraDTO(comprasApagar, comprasPagas, comprasVencidas, totalApagar, totalPago, totalVencido);
    }

    public List<DreDiarioDTO> gerarDreDiarioPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Venda> todasVendas = vendaService.listAll();
        List<Compra> todasCompras = compraService.findAll();
        List<DreDiarioDTO> relatorio = new ArrayList<>();
        double saldoAnterior = 0.0;

        // Calcula saldo anterior ao período
        for (Venda v : todasVendas) {
            if (v.getDataPagamentoDebito() != null && v.getDataPagamentoDebito().toLocalDate().isBefore(dataInicio))
                saldoAnterior += v.getPagamentoDebito();
            if (v.getDataPagamentoCredito() != null && v.getDataPagamentoCredito().toLocalDate().isBefore(dataInicio))
                saldoAnterior += v.getPagamentoCredito();
        }
        for (Compra c : todasCompras) {
            if (c.isPago() && c.getDataPagamento() != null && c.getDataPagamento().toLocalDate().isBefore(dataInicio))
                saldoAnterior -= c.getValorTotal();
        }

        double saldoDia = saldoAnterior;

        for (LocalDate data = dataInicio; !data.isAfter(dataFim); data = data.plusDays(1)) {
            final LocalDate dataFinal = data; // Necessário para uso em lambda

            double valorReceber = todasVendas.stream()
                    .filter(v -> (v.getDataPagamentoDebito() != null && v.getDataPagamentoDebito().toLocalDate().isEqual(dataFinal)) ||
                            (v.getDataPagamentoCredito() != null && v.getDataPagamentoCredito().toLocalDate().isEqual(dataFinal)))
                    .mapToDouble(v -> {
                        double debito = (v.getDataPagamentoDebito() != null && v.getDataPagamentoDebito().toLocalDate().isEqual(dataFinal)) ? v.getPagamentoDebito() : 0.0;
                        double credito = (v.getDataPagamentoCredito() != null && v.getDataPagamentoCredito().toLocalDate().isEqual(dataFinal)) ? v.getPagamentoCredito() : 0.0;
                        return debito + credito;
                    }).sum();

            double valorPagar = todasCompras.stream()
                    .filter(c -> c.isPago() && c.getDataPagamento() != null && c.getDataPagamento().toLocalDate().isEqual(dataFinal))
                    .mapToDouble(Compra::getValorTotal)
                    .sum();

            double resultado = valorReceber - valorPagar;
            saldoDia += resultado;

            relatorio.add(new DreDiarioDTO(
                    dataFinal,
                    valorReceber,
                    valorPagar,
                    resultado,
                    saldoDia,
                    dataFinal.equals(dataInicio) ? saldoAnterior : null // saldoAnterior só no primeiro dia
            ));
        }
        return relatorio;
    }

}
