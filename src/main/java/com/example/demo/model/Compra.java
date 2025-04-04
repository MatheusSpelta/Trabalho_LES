package com.example.demo.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

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
    private Date dataCompra;
    private Date dataVencimento;

    private boolean isPago;
    private Date dataPagamento;
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
