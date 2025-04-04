package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.VendaProduto;
import com.example.demo.repository.VendaProdutoRepository;

@Service
public class VendaProdutoService {

    @Autowired
    private VendaProdutoRepository vendaProdutoRepository;

    public void salvar(VendaProduto vendaProduto) {
        vendaProdutoRepository.save(vendaProduto);
    }

    public void removerProdutosPorVenda(UUID vendaId) {
        vendaProdutoRepository.deleteByVendaId(vendaId);
    }

    public List<VendaProduto> findByVendaId(UUID vendaId, boolean ativo){
        return vendaProdutoRepository.findByVendaIdAndAtivo(vendaId, ativo);
    }
}
