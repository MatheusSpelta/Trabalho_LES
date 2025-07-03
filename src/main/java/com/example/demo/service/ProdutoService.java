package com.example.demo.service;

import com.example.demo.exception.ProdutoException;
import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationTypeNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final BalancaService balancaService;

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
        editado.setCodigo(produto.getCodigo());
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


    public double calcularKgProduto(String porta) {
        balancaService.init(porta);
        return balancaService.getUltimoPeso();

    }

    public Produto findByEan(String ean) {
        return produtoRepository.findByEAN(ean)
                .orElseThrow(ProdutoException::produtoNaoEncontrado);
    }
}
