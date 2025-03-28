package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.InterfacePermissao;
import com.example.demo.repository.InterfacePermissaoRepository;

@Service
public class InterfacePermissaoService {

    @Autowired
    private InterfacePermissaoRepository interfacePermissaoRepository;

    public InterfacePermissao salvar(InterfacePermissao interfacePermissao) {
        return interfacePermissaoRepository.save(interfacePermissao);
    }

    public boolean existsByDescricao(String descricao) {
        return interfacePermissaoRepository.existsByDescricao(descricao);
    }

    public List<InterfacePermissao> findAll() {
        return interfacePermissaoRepository.findAll();
    }

}
