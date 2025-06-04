package com.example.demo.repository;

import com.example.demo.model.Cliente;
import com.example.demo.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendaRepository extends JpaRepository<Venda, UUID> {

    List<Venda> findByClienteId(UUID clienteId);

    @Query("SELECT DISTINCT v.cliente FROM Venda v WHERE DATE(v.dataCriacao) = :data")
    List<Cliente> findClientesAtendidosPorDia(@Param("data") LocalDate data);

    Optional<Venda> findTopByClienteIdOrderByDataCriacaoDesc(UUID clienteId);

    @Query("SELECT v FROM Venda v WHERE v.cliente.id = :clienteId AND DATE(v.dataCriacao) = :data")
    List<Venda> findByClienteIdAndDataCriacao(@Param("clienteId") UUID clienteId, @Param("data") LocalDate data);

    @Query("SELECT v FROM Venda v WHERE v.isPago = false AND DATE(v.dataCriacao) BETWEEN :inicio AND :fim")
    List<Venda> findVendasNaoPagasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
