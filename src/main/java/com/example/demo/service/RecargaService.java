package com.example.demo.service;

import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Recarga;
import com.example.demo.repository.RecargaRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;

@Service
@Tag(name = "recarga", description = "Fornece serviços web REST para acesso e manipualação de Recargas.")
public class RecargaService {
    
    @Autowired
    private RecargaRepository recargaRepository;

    @Autowired
    private ClienteService clienteService;

    @Transactional
    public Recarga saveAll(Recarga recarga) throws RelationTypeNotFoundException {
        clienteService.RecargaDebito(recarga.getCliente().getId(),recarga.getValorRecarga());
        return recargaRepository.save(recarga);
    }

    @Transactional
    public Recarga editId(UUID id, Recarga recarga) throws RelationTypeNotFoundException {
        Recarga existingRecarga = recargaRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Recarga com id " + id + " não encontrada."));

        double valorDiferenca = recarga.getValorRecarga() - existingRecarga.getValorRecarga();
        clienteService.RecargaDebito(existingRecarga.getCliente().getId(), valorDiferenca);

        existingRecarga.setValorRecarga(recarga.getValorRecarga());
        existingRecarga.setCliente(recarga.getCliente());

        return recargaRepository.save(existingRecarga);
    }

    @Transactional
    public void desativarRecarga(UUID id) throws RelationTypeNotFoundException {
        Recarga recarga = recargaRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Recarga com id " + id + " não encontrada."));

        clienteService.RecargaDebito(recarga.getCliente().getId(), -recarga.getValorRecarga());
        recargaRepository.delete(recarga);
    }
}
