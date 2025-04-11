package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ProdutosDTO;
import com.example.demo.DTO.VendaDTO;
import com.example.demo.DTO.VendaResponseDTO;
import com.example.demo.exception.ClienteException;
import com.example.demo.exception.VendaException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Venda;
import com.example.demo.model.VendaProduto;
import com.example.demo.repository.VendaRepository;

import jakarta.transaction.Transactional;

@Service
public class VendaService {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private VendaProdutoService vendaProdutoService;

    @Transactional
    public Venda realizarVenda(VendaDTO vendaDTO) {

        System.out.println(vendaDTO.codigoCartao());
        Cliente cliente = clienteService.findByCartao(vendaDTO.codigoCartao());
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        double valorTotal = vendaDTO.produtos().stream().mapToDouble(ProdutosDTO::valorTotal).sum();

        double valorRestante = valorTotal;
        double valorDebitoUtilizado = 0;
        double valorCreditoUtilizado = 0;

        // Substrair valor do saldo de débito
        if (cliente.getSaldoDebito() >= valorRestante) {
            valorDebitoUtilizado = valorRestante;
            cliente.setSaldoDebito(cliente.getSaldoDebito() - valorDebitoUtilizado);
            valorRestante -= valorDebitoUtilizado;
        } else {
            valorDebitoUtilizado = cliente.getSaldoDebito();
            cliente.setSaldoDebito(0);
            valorRestante -= valorDebitoUtilizado;
        }

        // Substrair valor do saldo de crédito
        if (valorRestante > 0) {
            if (cliente.getLimiteCredito() >= valorRestante) {
                valorCreditoUtilizado = valorRestante;
                cliente.setLimiteCredito(cliente.getLimiteCredito() - valorCreditoUtilizado);
                valorRestante -= valorCreditoUtilizado;
            } else {
                throw VendaException.saldoInsuficiente();
            }
        }

        // Registra a venda
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setValorTotal(valorTotal);
        venda.setPagamentoCredito(valorCreditoUtilizado);
        venda.setPagamentoDebito(valorDebitoUtilizado);
        venda.setPago(valorCreditoUtilizado == 0);
        venda.setDataVenda(LocalDateTime.now());
        venda = vendaRepository.save(venda);

        // Registrar os produtos vendidos
        for (ProdutosDTO produtoDTO : vendaDTO.produtos()) {
            VendaProduto vendaProduto = new VendaProduto();
            vendaProduto.setVenda(venda);
            vendaProduto.setProduto(produtoDTO.produto());
            vendaProduto.setQuantidade(produtoDTO.quantidade());
            vendaProduto.setValorUnitario(produtoDTO.produto().getValorVenda());
            vendaProduto.setValorTotal(produtoDTO.valorTotal());
            vendaProduto.setProduto(produtoDTO.produto());

            vendaProdutoService.salvar(vendaProduto);
        }

        return venda;
    }

    @Transactional
    public Venda editarVenda(UUID id, VendaDTO vendaDTO) {
        // Buscar a venda existente
        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda com id " + id + " não encontrada."));

        // Buscar o cliente associado à venda
        Cliente cliente = vendaExistente.getCliente();

        // Reverter os valores de débito e crédito do cliente
        cliente.setSaldoDebito(cliente.getSaldoDebito() + vendaExistente.getPagamentoDebito());
        cliente.setLimiteCredito(cliente.getLimiteCredito() + vendaExistente.getPagamentoCredito());

        // Calcular o novo valor total da venda
        double novoValorTotal = vendaDTO.produtos().stream().mapToDouble(ProdutosDTO::valorTotal).sum();

        double valorRestante = novoValorTotal;
        double novoValorDebitoUtilizado = 0;
        double novoValorCreditoUtilizado = 0;

        // Subtrair o novo valor do saldo de débito
        if (cliente.getSaldoDebito() >= valorRestante) {
            novoValorDebitoUtilizado = valorRestante;
            cliente.setSaldoDebito(cliente.getSaldoDebito() - novoValorDebitoUtilizado);
            valorRestante -= novoValorDebitoUtilizado;
        } else {
            novoValorDebitoUtilizado = cliente.getSaldoDebito();
            cliente.setSaldoDebito(0);
            valorRestante -= novoValorDebitoUtilizado;
        }

        // Subtrair o novo valor do saldo de crédito
        if (valorRestante > 0) {
            if (cliente.getLimiteCredito() >= valorRestante) {
                novoValorCreditoUtilizado = valorRestante;
                cliente.setLimiteCredito(cliente.getLimiteCredito() - novoValorCreditoUtilizado);
                valorRestante -= novoValorCreditoUtilizado;
            } else {
                throw VendaException.saldoInsuficiente();
            }
        }

        // Atualizar os dados da venda
        vendaExistente.setValorTotal(novoValorTotal);
        vendaExistente.setPagamentoDebito(novoValorDebitoUtilizado);
        vendaExistente.setPagamentoCredito(novoValorCreditoUtilizado);
        vendaExistente.setPago(novoValorCreditoUtilizado == 0);
        vendaExistente.setDataVenda(LocalDateTime.now());

        // Atualizar os produtos da venda
        List<VendaProduto> produtosAtuais = vendaProdutoService.findByVendaId(vendaExistente.getId(), true);

        // Marcar todos os produtos atuais como inativos
        produtosAtuais.forEach(produto -> produto.setAtivo(false));

        // Atualizar ou criar novos registros de produtos
        for (ProdutosDTO produtoDTO : vendaDTO.produtos()) {
            VendaProduto vendaProdutoExistente = produtosAtuais.stream()
                    .filter(produto -> produto.getProduto().getId().equals(produtoDTO.produto().getId()))
                    .findFirst()
                    .orElse(null);

            if (vendaProdutoExistente != null) {
                // Produto já existe, apenas atualiza os valores e reativa
                vendaProdutoExistente.setQuantidade(produtoDTO.quantidade());
                vendaProdutoExistente.setValorUnitario(produtoDTO.produto().getValorVenda());
                vendaProdutoExistente.setValorTotal(produtoDTO.valorTotal());
                vendaProdutoExistente.setAtivo(true);
            } else {
                // Produto não existe, cria um novo registro
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

        // Salvar os produtos atualizados
        produtosAtuais.forEach(vendaProdutoService::salvar);

        // Salvar a venda atualizada
        return vendaRepository.save(vendaExistente);
    }

    public List<VendaResponseDTO> listarVendasPorCartaoCliente(String codigoCartao, boolean ativo) {
        Cliente cliente = clienteService.findByCartao(codigoCartao);
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        return vendas.stream().map(venda -> {
            List<VendaProduto> produtos = vendaProdutoService.findByVendaId(venda.getId(), ativo);
            return new VendaResponseDTO(venda, produtos);
        }).toList();
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
}
