package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    
    Optional<Cliente> findByMatricula(String matricula);
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByCartao(String cartao);
}
