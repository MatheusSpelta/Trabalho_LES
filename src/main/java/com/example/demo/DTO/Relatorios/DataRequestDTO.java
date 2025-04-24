package com.example.demo.DTO.Relatorios;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DataRequestDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
}
