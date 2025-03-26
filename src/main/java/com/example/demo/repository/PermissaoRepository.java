package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Permissao;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, UUID> {

    Optional<Permissao> findByFuncionarioIdAndTipoPermissaoIdAndInterfacePermissaoId(UUID funcionarioId,
            UUID tipoPermissaoId, UUID interfacePermissaoId);

    List<Permissao> findByFuncionarioId(UUID funcionarioId);
}
