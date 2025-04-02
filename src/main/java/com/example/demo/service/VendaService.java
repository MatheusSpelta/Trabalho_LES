package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ProdutosDTO;
import com.example.demo.DTO.VendaDTO;
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
}
