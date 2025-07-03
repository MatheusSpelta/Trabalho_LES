package com.example.demo.service;

import com.example.demo.model.Compra;
import com.example.demo.repository.CompraRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationTypeNotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final ClienteService clienteService;

    public Compra saveAll(Compra compra) {
        return compraRepository.save(compra);
    }

    public Compra editId(UUID id, Compra compra) throws RelationTypeNotFoundException {
        Compra editado = compraRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + id + " não encontrado."));

        editado.setCodigo(compra.getCodigo());
        editado.setDescricao(compra.getDescricao());
        editado.setFornecedor(compra.getFornecedor());
        editado.setValorTotal(compra.getValorTotal());
        editado.setDataCompra(compra.getDataCompra());
        editado.setDataVencimento(compra.getDataVencimento());
        editado.setPago(compra.isPago());
        editado.setDataPagamento(compra.getDataPagamento());

        return compraRepository.save(editado);
    }

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Compra findById(UUID id) throws RelationTypeNotFoundException {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + id + " não encontrado."));
    }

    public void changeAtivo(UUID id) throws RelationTypeNotFoundException {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + id + " não encontrado."));

        compra.setAtivo(!compra.isAtivo());
        compraRepository.save(compra);
    }

    public void changeIsPago(UUID id) throws RelationTypeNotFoundException {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + id + " não encontrado."));
        compra.setDataPagamento(LocalDateTime.now());
        compra.setPago(!compra.isPago());
        compraRepository.save(compra);
    }

    public List<Compra> findComprasVencidas() {
        return compraRepository.findByDataVencimentoBeforeAndIsPagoFalseAndAtivoTrue(new Date());
    }

    public List<Compra> findComprasByFornecedor(UUID fornecedorId) {
        return compraRepository.findByFornecedorId(fornecedorId);
    }

    public List<Compra> findComprasVencidasByFornecedor(UUID fornecedorId) {
        return compraRepository.findByFornecedorIdAndDataVencimentoBeforeAndIsPagoFalseAndAtivoTrue(fornecedorId,
                new Date());
    }

    public List<Compra> findComprasPagasByFornecedor(UUID fornecedorId) {
        return compraRepository.findByFornecedorIdAndIsPagoTrue(fornecedorId);
    }

    public void deletarCompra(UUID compraId) throws RelationTypeNotFoundException {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + compraId + " não encontrado."));
        compra.setAtivo(!compra.isAtivo());
        compraRepository.save(compra);
    }

}
