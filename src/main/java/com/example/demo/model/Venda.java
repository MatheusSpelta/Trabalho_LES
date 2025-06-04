package com.example.demo.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double valorTotal;

    private double pagamentoCredito;
    private double pagamentoDebito;
    private double valorEmAberto;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private boolean isPago;

    private boolean ativo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime dataPagamentoCredito;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime dataPagamentoDebito;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime dataPagamentoFinal;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime ultimaAlteracao;

    @PreUpdate
    public void preUpdate() {
        ultimaAlteracao = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    @PrePersist
    public void prePersist() {
        dataCriacao = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

    }

}
