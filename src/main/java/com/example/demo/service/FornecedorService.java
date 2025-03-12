package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Fornecedor;
import com.example.demo.repository.FornecedorRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Fornecedor Service", description = "Fornece serviços web REST para acesso e manipualação de Fornecedor")
public class FornecedorService {
    
    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Fornecedor saveAll(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    public Fornecedor editId(UUID id, Fornecedor fornecedor){
        Fornecedor editado = fornecedorRepository.findById(id).get();

        editado.setNome(fornecedor.getNome());
        editado.setTelefone(fornecedor.getTelefone());
        editado.setAtivo(fornecedor.isAtivo());
        editado.setCnpj(fornecedor.getCnpj());
        editado.setEndereco(fornecedor.getEndereco());
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
