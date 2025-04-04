package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, UUID> {

    List<Venda> findByClienteId(UUID clienteId);

    List<Venda> findByClienteIdAndAtivoTrue(UUID clienteId);
}
