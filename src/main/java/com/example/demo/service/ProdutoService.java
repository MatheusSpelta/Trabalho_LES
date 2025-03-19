package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto saveAll(Produto produto) {
        if (produto.getEAN() != null) {
            produtoRepository.findByEAN(produto.getEAN()).ifPresent(p -> {
                throw new IllegalArgumentException("EAN já cadastrado");
            });
        }
        if (produto.getCodigo() != null) {
            produtoRepository.findByCodigo(produto.getCodigo()).ifPresent(p -> {
                throw new IllegalArgumentException("Código já cadastrado");
            });
        }
        return produtoRepository.save(produto);
    }

    public Produto editId(UUID id, Produto produto) throws RelationTypeNotFoundException {
        Produto editado = produtoRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Produto com id " + id + " não encontrado."));

        if (produto.getEAN() != null) {
            produtoRepository.findByEAN(produto.getEAN()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw new IllegalArgumentException("EAN já cadastrado");
                }
            });
        }
        if (produto.getCodigo() != null) {
            produtoRepository.findByCodigo(produto.getCodigo()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw new IllegalArgumentException("Código já cadastrado");
                }
            });
        }

        editado.setDescricao(produto.getDescricao());
        editado.setEAN(produto.getEAN());
        editado.setValorVenda(produto.getValorVenda());
        editado.setValorCusto(produto.getValorCusto());
        editado.setMargemLucro(produto.getMargemLucro());
        editado.setCodigo(produto.getCodigo());
        editado.setMargemPromocao(produto.getMargemPromocao());
        editado.setPromocao(produto.isPromocao());
        editado.setKg(produto.isKg());
        editado.setAtivo(produto.isAtivo());

        return produtoRepository.save(editado);
    }

    public Produto findById(UUID id) throws RelationTypeNotFoundException {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Produto com id " + id + " não encontrado."));
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public List<Produto> findAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    public void changeAtivo(UUID id) throws RelationTypeNotFoundException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Produto com id " + id + " não encontrado."));

        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }

    public void changePromocao(UUID id) throws RelationTypeNotFoundException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Produto com id " + id + " não encontrado."));

        produto.setPromocao(!produto.isPromocao());
        produtoRepository.save(produto);
    }

    public void changeKg(UUID id) throws RelationTypeNotFoundException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Produto com id " + id + " não encontrado."));

        produto.setKg(!produto.isKg());
        produtoRepository.save(produto);
    }
}
