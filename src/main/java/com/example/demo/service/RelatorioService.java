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

    public List<VendaDiariaDTO> obterVendasDiariaDetalhada(LocalDate data) {
        List<Venda> todasVendas = vendaService.listAll().stream()
                .filter(v -> v.getDataCriacao().toLocalDate().isEqual(data))
                .toList();

        return todasVendas.stream()
                .map(v -> new VendaDiariaDTO(
                        v.getCliente().getNome(), // ou outro campo desejado
                        v.getValorTotal(),
                        v.getDataCriacao()
                ))
                .toList();
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

    public DreDiarioDTO gerarDreDiarioPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Venda> todasVendas = vendaService.listAll();
        List<Compra> todasCompras = compraService.findAll();
        List<DreDiarioDTO.DreDiarioDias> relatorio = new ArrayList<>();
        double saldoAnterior = getSaldoAnterior(dataInicio, todasVendas, todasCompras);

        double saldoDia = saldoAnterior;

        for (LocalDate data = dataInicio; !data.isAfter(dataFim); data = data.plusDays(1)) {
            final LocalDate dataFinal = data;

            double valorReceber = todasVendas.stream()
                    .filter(v -> v.isPago() &&
                            v.getDataPagamentoFinal() != null &&
                            v.getDataPagamentoFinal().toLocalDate().isEqual(dataFinal))
                    .mapToDouble(Venda::getValorTotal)
                    .sum();

            double valorPagar = todasCompras.stream()
                    .filter(c -> c.isPago() && c.getDataPagamento() != null && c.getDataPagamento().toLocalDate().isEqual(dataFinal))
                    .mapToDouble(Compra::getValorTotal)
                    .sum();

            double resultado = valorReceber - valorPagar;
            saldoDia += resultado;

            relatorio.add(new DreDiarioDTO.DreDiarioDias(
                    dataFinal,
                    valorReceber,
                    valorPagar,
                    resultado,
                    saldoDia,
                    dataFinal.equals(dataInicio) ? saldoAnterior : null
            ));
        }
        return new DreDiarioDTO(relatorio, saldoAnterior);
    }

    private static double getSaldoAnterior(LocalDate dataInicio, List<Venda> todasVendas, List<Compra> todasCompras) {
        double saldoAnterior = 0.0;

        for (Venda v : todasVendas) {
            if (v.isPago() && v.getDataPagamentoFinal() != null && v.getDataPagamentoFinal().toLocalDate().isBefore(dataInicio)) {
                saldoAnterior += v.getValorTotal();
            }
        }
        for (Compra c : todasCompras) {
            if (c.isPago() && c.getDataPagamento() != null && c.getDataPagamento().toLocalDate().isBefore(dataInicio)) {
                saldoAnterior -= c.getValorTotal();
            }
        }
        return saldoAnterior;
    }

}
