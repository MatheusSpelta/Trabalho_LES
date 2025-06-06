package com.example.demo.service;

import com.example.demo.DTO.ProdutosDTO;
import com.example.demo.DTO.Relatorios.DataRequestDTO;
import com.example.demo.DTO.ValorAbertoDTO;
import com.example.demo.DTO.VendaDTO;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.exception.ClienteException;
import com.example.demo.exception.VendaException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Produto;
import com.example.demo.model.Venda;
import com.example.demo.model.VendaProduto;
import com.example.demo.repository.VendaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VendaService {

    private final ClienteService clienteService;
    private final VendaRepository vendaRepository;
    private final VendaProdutoService vendaProdutoService;
    private final ImpressoraTermicaService impressoraTermicaService;

    @Transactional
    public Venda realizarVenda(VendaDTO vendaDTO) {
        Cliente cliente = clienteService.findByCartao(vendaDTO.codigoCartao());
        if (cliente == null) throw ClienteException.clienteNaoEncontrado();
        if (vendaDTO.produtos().isEmpty()) throw VendaException.produtosNaoInformados();

        double valorTotal = vendaDTO.produtos().stream().mapToDouble(ProdutosDTO::valorTotal).sum();

        // Permite saldoDebito negativo até o limite de crédito
        if (cliente.getSaldoDebito() - valorTotal < -cliente.getLimiteCredito()) {
            throw VendaException.saldoInsuficiente();
        }

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setValorTotal(valorTotal);

        // Se saldoDebito suficiente, registra como pago no débito
        if (cliente.getSaldoDebito() >= valorTotal) {
            cliente.setSaldoDebito(cliente.getSaldoDebito() - valorTotal);
            venda.setPagamentoDebito(valorTotal);
            venda.setPagamentoCredito(0);
            venda.setValorEmAberto(0);
            venda.setPago(true);
            venda.setDataPagamentoDebito(ZonedDateTime.now());
            venda.setDataPagamentoFinal(ZonedDateTime.now());
        } else {
            // Parte no débito, parte no crédito
            double valorDebito = Math.max(cliente.getSaldoDebito(), 0);
            double valorCredito = valorTotal - valorDebito;
            cliente.setSaldoDebito(cliente.getSaldoDebito() - valorTotal);

            venda.setPagamentoDebito(valorDebito);
            venda.setPagamentoCredito(valorCredito);
            venda.setValorEmAberto(valorCredito);
            venda.setPago(false);
            venda.setDataPagamentoDebito(valorDebito > 0 ? ZonedDateTime.now() : null);
            venda.setDataPagamentoCredito(null); // Só será preenchida quando quitar o crédito
            venda.setDataPagamentoFinal(null);
        }

//        venda.getDataPagamentoFinal(ZonedDateTime.now());
        venda = vendaRepository.save(venda);

        for (ProdutosDTO produtoDTO : vendaDTO.produtos()) {
            Produto produto = produtoDTO.produto();
            VendaProduto vendaProduto = new VendaProduto();
            vendaProduto.setVenda(venda);
            vendaProduto.setProduto(produto);
            vendaProduto.setQuantidade(produtoDTO.quantidade());
            vendaProduto.setValorUnitario(produto.getValorVenda());
            vendaProduto.setValorTotal(produtoDTO.valorTotal());
            vendaProdutoService.salvar(vendaProduto);
        }

        return venda;
    }

    @Transactional
    public Venda editarVenda(UUID id, VendaDTO vendaDTO) {
        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda com id " + id + " não encontrada."));
        Cliente cliente = vendaExistente.getCliente();

        // Reverter saldo de débito do cliente
        cliente.setSaldoDebito(cliente.getSaldoDebito() + vendaExistente.getPagamentoDebito() + vendaExistente.getPagamentoCredito());

        double novoValorTotal = vendaDTO.produtos().stream().mapToDouble(ProdutosDTO::valorTotal).sum();

        // Permite saldoDebito negativo até o limite de crédito
        if (cliente.getSaldoDebito() - novoValorTotal < -cliente.getLimiteCredito()) {
            throw VendaException.saldoInsuficiente();
        }

        double valorDebito = Math.max(cliente.getSaldoDebito(), 0);
        double valorCredito = novoValorTotal - valorDebito;
        cliente.setSaldoDebito(cliente.getSaldoDebito() - novoValorTotal);

        vendaExistente.setValorTotal(novoValorTotal);
        vendaExistente.setPagamentoDebito(valorDebito);
        vendaExistente.setPagamentoCredito(valorCredito);
        vendaExistente.setValorEmAberto(valorCredito);

        if (valorCredito == 0) {
            vendaExistente.setPago(true);
            vendaExistente.setDataPagamentoDebito(ZonedDateTime.now());
            vendaExistente.setDataPagamentoCredito(null);
            vendaExistente.setDataPagamentoFinal(ZonedDateTime.now());
        } else {
            vendaExistente.setPago(false);
            vendaExistente.setDataPagamentoDebito(valorDebito > 0 ? ZonedDateTime.now() : null);
            vendaExistente.setDataPagamentoCredito(null);
            vendaExistente.setDataPagamentoFinal(null);
        }

        // Atualize produtos conforme já implementado
        List<VendaProduto> produtosAtuais = vendaProdutoService.findByVendaId(vendaExistente.getId(), true);
        produtosAtuais.forEach(produto -> produto.setAtivo(false));
        for (ProdutosDTO produtoDTO : vendaDTO.produtos()) {
            VendaProduto vendaProdutoExistente = produtosAtuais.stream()
                    .filter(produto -> produto.getProduto().getId().equals(produtoDTO.produto().getId()))
                    .findFirst()
                    .orElse(null);

            if (vendaProdutoExistente != null) {
                vendaProdutoExistente.setQuantidade(produtoDTO.quantidade());
                vendaProdutoExistente.setValorUnitario(produtoDTO.produto().getValorVenda());
                vendaProdutoExistente.setValorTotal(produtoDTO.valorTotal());
                vendaProdutoExistente.setAtivo(true);
            } else {
                VendaProduto novoVendaProduto = new VendaProduto();
                novoVendaProduto.setVenda(vendaExistente);
                novoVendaProduto.setProduto(produtoDTO.produto());
                novoVendaProduto.setQuantidade(produtoDTO.quantidade());
                novoVendaProduto.setValorUnitario(produtoDTO.produto().getValorVenda());
                novoVendaProduto.setValorTotal(produtoDTO.valorTotal());
                novoVendaProduto.setAtivo(true);
                vendaProdutoService.salvar(novoVendaProduto);
            }
        }
        produtosAtuais.forEach(vendaProdutoService::salvar);

        return vendaRepository.save(vendaExistente);
    }

    public List<VendaResponseDTO> listarTodasVendasPorCartaoCliente(String codigoCartao) {
        Cliente cliente = clienteService.findByCartao(codigoCartao);
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        return vendas.stream().map(venda -> {
            List<VendaProduto> produtos = vendaProdutoService.findByVendaId(venda.getId());
            return new VendaResponseDTO(venda, produtos);
        }).toList();
    }

    public List<VendaResponseDTO> listarTodasVendasPorClienteId(UUID clienteId) {
        Cliente cliente = clienteService.findById(clienteId);
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        return vendas.stream().map(venda -> {
            List<VendaProduto> produtos = vendaProdutoService.findByVendaId(venda.getId());
            return new VendaResponseDTO(venda, produtos);
        }).toList();
    }

    public List<VendaResponseDTO> listarVendasPorClienteId(UUID clienteId) {
        List<Venda> vendas = vendaRepository.findByClienteId(clienteId);
        return vendas.stream()
                .map(venda -> {
                    List<VendaProduto> produtos = vendaProdutoService.findByVendaId(venda.getId());
                    return new VendaResponseDTO(venda, produtos);
                }).toList();
    }

    public VendaResponseDTO findVendaById(UUID vendaId) {
        // Buscar a venda pelo ID
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda com id " + vendaId + " não encontrada."));

        // Buscar os produtos associados à venda
        List<VendaProduto> produtos = vendaProdutoService.findByVendaId(vendaId, true);

        // Retornar a venda e seus produtos no DTO de resposta
        return new VendaResponseDTO(venda, produtos);
    }

    public List<Venda> listAll() {
        return vendaRepository.findAll();
    }

    public List<Cliente> findClientesAtendidos(LocalDate data) {
        return vendaRepository.findClientesAtendidosPorDia(data);
    }

    public void reimprimirVenda(UUID vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda com id " + vendaId + " não encontrada."));

        List<VendaProduto> produtos = vendaProdutoService.findByVendaId(vendaId);

        Cliente cliente = venda.getCliente();

        // Passar saldo em débito e limite de crédito disponível
        impressoraTermicaService.imprimirRecibo(
                cliente,
                venda,
                produtos,
                venda.getValorTotal(),
                venda.getPagamentoCredito(),
                venda.getPagamentoDebito(),
                cliente.getSaldoDebito(),
                cliente.getLimiteCredito()
        );
    }

    public void imprimirUltimaVenda(String codigoCartao) {
        Cliente cliente = clienteService.findByCartao(codigoCartao);
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        Venda ultimaVenda = vendaRepository.findTopByClienteIdOrderByDataCriacaoDesc(cliente.getId())
                .orElseThrow(() -> new RuntimeException("Nenhuma venda encontrada para o cliente com cartão " + codigoCartao));

        List<VendaProduto> produtos = vendaProdutoService.findByVendaId(ultimaVenda.getId());

        impressoraTermicaService.imprimirRecibo(
                cliente,
                ultimaVenda,
                produtos,
                ultimaVenda.getValorTotal(),
                ultimaVenda.getPagamentoCredito(),
                ultimaVenda.getPagamentoDebito(),
                cliente.getSaldoDebito(),
                cliente.getLimiteCredito()
        );
    }

    public LocalDateTime ajustarParaFusoHorarioBrasil(LocalDateTime dataHora) {
        ZoneId fusoHorarioBrasil = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime dataHoraBrasil = dataHora.atZone(ZoneId.systemDefault()).withZoneSameInstant(fusoHorarioBrasil);
        return dataHoraBrasil.toLocalDateTime();
    }

    public List<ValorAbertoDTO> listarVendasEmAberto(DataRequestDTO dataRequest) {
        LocalDate inicio = dataRequest.getDataInicio();
        LocalDate fim = dataRequest.getDataFim();

        // Buscar todas as vendas em aberto no período
        List<Venda> vendasEmAberto = vendaRepository.findVendasNaoPagasPorPeriodo(inicio, fim);

        // Agrupar vendas por cliente
        Map<Cliente, List<Venda>> vendasPorCliente = vendasEmAberto.stream()
                .collect(Collectors.groupingBy(Venda::getCliente));

        // Criar a lista de ValorAbertoDTO
        List<ValorAbertoDTO> valoresAbertos = vendasPorCliente.entrySet().stream()
                .map(entry -> {
                    Cliente cliente = entry.getKey();
                    List<Venda> vendas = entry.getValue();
                    float valorTotal = (float) vendas.stream()
                            .mapToDouble(Venda::getPagamentoCredito)
                            .sum();
                    return new ValorAbertoDTO(cliente, vendas, valorTotal);
                })
                .toList();

        return valoresAbertos;
    }

    public Cliente quitarDebito(UUID clienteId) {
        List<Venda> vendas = vendaRepository.findByClienteId(clienteId);
        Cliente cliente = clienteService.findById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado!");
        }
        for (Venda venda : vendas) {
            if (!venda.isPago()) {
                venda.setPago(true);
                venda.setDataPagamentoCredito(ZonedDateTime.now());
                venda.setDataPagamentoFinal(ZonedDateTime.now());
                venda.setValorEmAberto(0);
                vendaRepository.save(venda);
            }
            cliente.setSaldoDebito(0.0);

        }
        return clienteService.salvar(cliente);
    }

}
