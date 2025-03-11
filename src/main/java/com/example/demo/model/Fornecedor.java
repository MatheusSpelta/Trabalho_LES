package com.example.demo.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Fornecedor extends Usuario{
    
    private String cnpj;
    private String razaoSocial;
    
}
