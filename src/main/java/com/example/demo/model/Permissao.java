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
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "tipo_Permissao_Id")
    private TipoPermissao tipoPermissao;

    @ManyToOne
    @JoinColumn(name = "funcionario_Id")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "interface_Id")
    private InterfacePermissao interfacePermissao;

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
