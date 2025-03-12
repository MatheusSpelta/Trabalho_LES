package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Compra;
import com.example.demo.repository.CompraRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "compra", description = "Fornece serviços web REST para acesso e manipualação de Compras.")
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public Compra saveAll(Compra compra) {
        return compraRepository.save(compra);
    }

    public Compra editId(UUID id, Compra compra) throws RelationTypeNotFoundException{
        Compra editado = compraRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Compra com id " + id + " não encontrado."));
        
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

        compra.setPago(!compra.isPago());
        compraRepository.save(compra);
    }

    
}
