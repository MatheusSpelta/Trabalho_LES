package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Funcionario extends Usuario{
    
    private String CPF;
    private String email;
    private String senha;

    @ManyToOne
    @JoinColumn(name = "tipo_Usuario_Id")
    private TipoUsuario tipoUsuario;
}
