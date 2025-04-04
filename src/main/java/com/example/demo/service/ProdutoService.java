package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ProdutoException;
import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto saveAll(Produto produto) {
        if (produto.getEAN() != null) {
            produtoRepository.findByEAN(produto.getEAN()).ifPresent(p -> {
                throw ProdutoException.eanJaCadastrado();
            });
        }
        if (produto.getCodigo() != null) {
            produtoRepository.findByCodigo(produto.getCodigo()).ifPresent(p -> {
                throw ProdutoException.codigoJaCadastrado();
            });
        }
        return produtoRepository.save(produto);
    }

    public Produto editId(UUID id, Produto produto) throws RelationTypeNotFoundException {
        Produto editado = produtoRepository.findById(id)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);

        if (produto.getEAN() != null) {
            produtoRepository.findByEAN(produto.getEAN()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw ProdutoException.eanJaCadastrado();
                }
            });
        }
        if (produto.getCodigo() != null) {
            produtoRepository.findByCodigo(produto.getCodigo()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw ProdutoException.codigoJaCadastrado();
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

    public Produto findById(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public List<Produto> findAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    public void changeAtivo(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);

        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }

    public void changePromocao(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);

        produto.setPromocao(!produto.isPromocao());
        produtoRepository.save(produto);
    }

    public void changeKg(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);

        produto.setKg(!produto.isKg());
        produtoRepository.save(produto);
    }

    public Produto findByEan(String ean) {
        return produtoRepository.findByEAN(ean)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);
    }
}
