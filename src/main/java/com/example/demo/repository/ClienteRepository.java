package com.example.demo.repository;

import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByMatricula(String matricula);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByCartao(String cartao);

    @Query("SELECT c FROM Cliente c WHERE EXTRACT(MONTH FROM c.dataNascimento) = :mes AND EXTRACT(DAY FROM c.dataNascimento) = :dia")
    List<Cliente> findClientesAniversariantes(@Param("mes") int mes, @Param("dia") int dia);

    @Query("SELECT COUNT(v) > 0 FROM Venda v WHERE v.cliente.id = :clienteId AND v.isPago = false AND v.dataCriacao >= :dataLimite")
    boolean existsVendaNaoPagaNosUltimosDias(@Param("clienteId") UUID clienteId, @Param("dataLimite") ZonedDateTime dataLimite);

}
