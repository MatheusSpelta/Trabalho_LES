package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Permissao;
import com.example.demo.repository.PermissaoRepository;

@Service
public class PermissaoService {

    @Autowired
    private PermissaoRepository permissaoRepository;

    public Permissao salvar(Permissao permissao) {
        return permissaoRepository.save(permissao);
    }

    public Optional<Permissao> findByFuncionarioAndTipoPermissaoAndInterfacePermissao(
            UUID funcionario, UUID tipoPermissao, UUID interfacePermissao) {
        return permissaoRepository.findByFuncionarioIdAndTipoPermissaoIdAndInterfacePermissaoId(
                funcionario, tipoPermissao, interfacePermissao);
    }

    public List<Permissao> findByFuncionario(UUID funcionarioId) {
        return permissaoRepository.findByFuncionarioId(funcionarioId);
    }
}
