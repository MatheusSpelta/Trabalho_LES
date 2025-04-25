package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    private String codigo;
    private String descricao;
    private double valorTotal;
    private LocalDateTime dataCompra;
    private LocalDateTime dataVencimento;

    private boolean isPago;
    private LocalDateTime dataPagamento;
    private boolean ativo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime ultimaAlteracao;

    @PreUpdate
    public void preUpdate() {
        ultimaAlteracao = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }

    @PrePersist
    public void prePersist() {
        final LocalDateTime atual = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
        dataCriacao = atual;
        ultimaAlteracao = atual;
    }
}
