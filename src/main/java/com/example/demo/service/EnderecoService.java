package com.example.demo.service;

import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Endereco;
import com.example.demo.repository.EnderecoRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "endereco", description = "Fornece serviços web REST para acesso e manipualação de Endereços.")
public class EnderecoService {
    
    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco saveAll(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco editId(UUID id, Endereco endereco) throws RelationTypeNotFoundException {
        Endereco editado = enderecoRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Endereco com id " + id + " não encontrado."));
        
        editado.setCep(endereco.getCep());
        editado.setRua(endereco.getRua());
        editado.setBairro(endereco.getBairro());
        editado.setCidade(endereco.getCidade());
        editado.setEstado(endereco.getEstado());
        editado.setPais(endereco.getPais());
        editado.setComplemento(endereco.getComplemento());       
        editado.setNumero(endereco.getNumero());

        return enderecoRepository.save(editado);
    }

    public Endereco findById(UUID id) throws RelationTypeNotFoundException {
        return enderecoRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Endereco com id " + id + " não encontrado."));
    }

}
