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
        if (cliente == null) {
            throw ClienteException.clienteNaoEncontrado();
        }

        if (vendaDTO.produtos().isEmpty()) {
            throw VendaException.produtosNaoInformados();
        }

        double valorTotal = vendaDTO.produtos().stream().mapToDouble(ProdutosDTO::valorTotal).sum();

        double valorRestante = valorTotal;
        double valorDebitoUtilizado = 0;
        double valorCreditoUtilizado = 0;

        // Subtrair valor do saldo de débito
        if (cliente.getSaldoDebito() >= valorRestante) {
            valorDebitoUtilizado = valorRestante;
            cliente.setSaldoDebito(cliente.getSaldoDebito() - valorDebitoUtilizado);
            valorRestante -= valorDebitoUtilizado;
        } else {
            valorDebitoUtilizado = cliente.getSaldoDebito();
            cliente.setSaldoDebito(0);
            valorRestante -= valorDebitoUtilizado;
        }

        // Subtrair valor do saldo de crédito
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
        if (venda.isPago()) {
            venda.setDataPagamento(LocalDateTime.now());
        }

        // Ajusta a data e hora para o fuso horário do Brasil
        venda.setDataVenda(ajustarParaFusoHorarioBrasil(LocalDateTime.now()));

        venda = vendaRepository.save(venda);

        // Registrar os produtos vendidos
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

        Venda ultimaVenda = vendaRepository.findTopByClienteIdOrderByDataVendaDesc(cliente.getId())
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

}
