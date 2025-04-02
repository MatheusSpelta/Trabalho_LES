package com.example.demo.service;

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
}
