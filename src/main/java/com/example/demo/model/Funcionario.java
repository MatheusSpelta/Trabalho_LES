package com.example.demo.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Funcionario extends Usuario{
    
    private String cpf;
    private String email;
    private String senha;

}
