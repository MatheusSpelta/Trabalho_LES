package com.example.demo.controller;

import com.example.demo.model.Produto;
import com.example.demo.service.ImpressoraEtiquetaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/etiqueta")
@AllArgsConstructor
public class ImpressoraEtiquetaController {

    private final ImpressoraEtiquetaService impressoraEtiquetaService;

    @PostMapping("/imprimir")
    public void imprimirEtiqueta(@RequestBody Produto produto) {
        impressoraEtiquetaService.imprimirEtiqueta(produto);
    }

}
