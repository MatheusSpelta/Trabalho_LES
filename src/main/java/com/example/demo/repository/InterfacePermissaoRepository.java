package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.InterfacePermissao;

@Repository
public interface InterfacePermissaoRepository extends JpaRepository<InterfacePermissao, UUID> {

    boolean existsByDescricao(String descricao);
}
