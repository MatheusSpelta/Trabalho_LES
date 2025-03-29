package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.PermissaoException;
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

    public List<Permissao> editar(List<Permissao> permissoes) {
        for (Permissao permissao : permissoes) {
            if (permissao.getId() != null && permissaoRepository.existsById(permissao.getId())) {
                permissaoRepository.save(permissao);
            } else {
                throw new IllegalArgumentException("Permissão com ID " + permissao.getId() + " não encontrada.");
            }
        }
        return permissoes;
    }

    public Permissao editarId(UUID id, Permissao permissao) {
        try {
            // Busca a permissão existente no banco de dados
            Permissao permissaoExistente = permissaoRepository.findById(id)
                    .orElseThrow(PermissaoException::permissaoNaoEncotnrada);

            // Atualiza os campos necessários
            permissaoExistente.setAtivo(permissao.isAtivo());

            // Salva a permissão atualizada no banco de dados
            return permissaoRepository.save(permissaoExistente);
        } catch (Exception e) {
            // Loga o erro para depuração
            System.err.println("Erro ao editar a permissão: " + e.getMessage());
            e.printStackTrace();

            // Relança a exceção para ser tratada em outro nível
            throw new RuntimeException("Erro ao editar a permissão. Verifique os dados e tente novamente.", e);
        }
    }

}
