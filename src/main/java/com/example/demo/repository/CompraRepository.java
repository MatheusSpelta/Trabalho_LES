package com.example.demo.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, UUID> {
    
    List<Compra> findByDataVencimentoBeforeAndIsPagoFalseAndAtivoTrue(Date dataAtual);

    List<Compra> findByFornecedorId(UUID fornecedorId);

    List<Compra> findByFornecedorIdAndDataVencimentoBeforeAndIsPagoFalseAndAtivoTrue(UUID fornecedorId,
            Date dataAtual);

    List<Compra> findByFornecedorIdAndIsPagoTrue(UUID fornecedorId);
}
