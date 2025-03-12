package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.FornecedorDTO;
import com.example.demo.model.Endereco;
import com.example.demo.model.Fornecedor;
import com.example.demo.repository.FornecedorRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Fornecedor Service", description = "Fornece serviços web REST para acesso e manipualação de Fornecedor")
public class FornecedorService {
    
    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EnderecoService enderecoService;

    public Fornecedor saveAll(FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = fornecedorDTO.fornecedor();
        Endereco endereco = fornecedorDTO.endereco();

        Endereco end = enderecoService.saveAll(endereco);
        fornecedor.setEndereco(end);

        return fornecedorRepository.save(fornecedor);
    }

     public Fornecedor editId(UUID id, FornecedorDTO fornecedorDTO) throws RelationTypeNotFoundException {
        Fornecedor fornecedor = fornecedorDTO.fornecedor();
        Endereco endereco = fornecedorDTO.endereco();

        Fornecedor editado = fornecedorRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Fornecedor com id " + id + " não encontrado."));

        enderecoService.editId(endereco.getId(),endereco);
        editado.setEndereco(endereco);

        editado.setNome(fornecedor.getNome());
        editado.setTelefone(fornecedor.getTelefone());
        editado.setAtivo(fornecedor.isAtivo());
        editado.setCnpj(fornecedor.getCnpj());
        editado.setRazaoSocial(fornecedor.getRazaoSocial());

        return fornecedorRepository.save(editado);
    }

    public List<Fornecedor> findAll() {
        return fornecedorRepository.findAll();
    }

    public Fornecedor findById(UUID id) {
        return fornecedorRepository.findById(id).get();
    }

    public void changeAtivo(UUID id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).get();

        fornecedor.setAtivo(!fornecedor.isAtivo());
        fornecedorRepository.save(fornecedor);
    }
}
