package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "usuario", description = "Fornece serviços web REST para acesso e manipualação de Usuários.")
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario saveAll(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario editId(UUID id, Usuario usuario) throws RelationTypeNotFoundException {
        Usuario editado = usuarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Usuário com id " + id + " não encontrado."));
        
        editado.setNome(usuario.getNome());
        editado.setEndereco(usuario.getEndereco());
        editado.setTelefone(usuario.getTelefone());
        editado.setAtivo(usuario.isAtivo());
 
        return usuarioRepository.save(editado);
    }

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario findById(UUID id) throws RelationTypeNotFoundException {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Usuário com id " + id + " não encontrado."));
    }

    public void changeAtivo(UUID id) throws RelationTypeNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Usuário com id " + id + " não encontrado."));

        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);
    }
}
