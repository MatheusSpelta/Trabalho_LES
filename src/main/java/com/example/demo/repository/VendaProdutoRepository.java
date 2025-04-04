package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.VendaProduto;

@Repository
public interface VendaProdutoRepository extends JpaRepository<VendaProduto, UUID> {

    @Modifying
    @Query("DELETE FROM VendaProduto vp WHERE vp.venda.id = :vendaId")
    void deleteByVendaId(@Param("vendaId") UUID vendaId);

    List<VendaProduto> findByVendaIdAndAtivo(UUID vendaId, boolean ativo);

}
