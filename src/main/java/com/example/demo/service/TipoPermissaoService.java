package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.TipoPermissao;
import com.example.demo.repository.TipoPermissaoRepository;

@Service
public class TipoPermissaoService {

    @Autowired
    private TipoPermissaoRepository tipoPermissaoRepository;

    public TipoPermissao salvar(TipoPermissao tipoPermissao) {
        return tipoPermissaoRepository.save(tipoPermissao);
    }

    public boolean existsByDescricao(String descricao) {
        return tipoPermissaoRepository.existsByDescricao(descricao);
    }

    public List<TipoPermissao> findAll() {
        return tipoPermissaoRepository.findAll();
    }
}
