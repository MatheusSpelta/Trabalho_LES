package com.example.demo.model;

import java.util.Date;

import com.example.demo.Enum.StatusCliente;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario{
    
    private String cpf;
    private String matricula;
    private Date dataNascimento;
    private String cartao;
    private double saldoDebito;
    private double limiteCredito;

    @Enumerated(EnumType.STRING)
    private StatusCliente statusCliente;

    
}
