package com.example.demo.service;

import com.example.demo.model.VendaProduto;
import com.example.demo.repository.VendaProdutoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VendaProdutoService {


    private final VendaProdutoRepository vendaProdutoRepository;

    public void salvar(VendaProduto vendaProduto) {
        vendaProdutoRepository.save(vendaProduto);
    }

    public void removerProdutosPorVenda(UUID vendaId) {
        vendaProdutoRepository.deleteByVendaId(vendaId);
    }

    public List<VendaProduto> findByVendaId(UUID vendaId, boolean ativo) {
        return vendaProdutoRepository.findByVendaIdAndAtivo(vendaId, ativo);
    }

    public List<VendaProduto> findByVendaId(UUID vendaId) {
        return vendaProdutoRepository.findByVendaId(vendaId);
    }

    public void desativarVendaProduto(UUID vendaProdutoId) {
        VendaProduto vendaProduto = vendaProdutoRepository.findById(vendaProdutoId)
                .orElseThrow(() -> new RuntimeException("VendaProduto não encontrado com id: " + vendaProdutoId));
        vendaProduto.setAtivo(false);
        vendaProdutoRepository.save(vendaProduto);
    }
}
