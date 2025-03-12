package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.model.Funcionario;
import com.example.demo.repository.FuncionarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;


@Service
@Tag(name = "Funcionario", description = "Fornece serviços web REST para acesso e manipulação de dados de Funcionarios")
public class FuncionarioService {
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Funcionario saveAll(Funcionario funcionario) {
        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario editId(UUID id, Funcionario funcionario) throws RelationTypeNotFoundException{
        Funcionario editado = funcionarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Funcionario com id " + id + " não encontrado."));

        editado.setNome(funcionario.getNome());
        editado.setTelefone(funcionario.getTelefone());
        editado.setAtivo(funcionario.isAtivo());
        editado.setCpf(funcionario.getCpf());
        editado.setEndereco(funcionario.getEndereco());
        editado.setEmail(funcionario.getEmail());
        editado.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        editado.setTipoUsuario(funcionario.getTipoUsuario());

        return funcionarioRepository.save(editado);
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(UUID id) throws RelationTypeNotFoundException {
        return funcionarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Funcionario com id " + id + " não encontrado."));
    }

    public void changeAtivo(UUID id) throws RelationTypeNotFoundException {
        Funcionario funcionario = funcionarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Funcionario com id " + id + " não encontrado."));

        funcionario.setAtivo(!funcionario.isAtivo());

        funcionarioRepository.save(funcionario);
    }

    public void changePassword(UUID id, String senha) throws RelationTypeNotFoundException {
        Funcionario funcionario = funcionarioRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Funcionario com id " + id + " não encontrado."));

        funcionario.setSenha(passwordEncoder.encode(senha));
        funcionarioRepository.save(funcionario);
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Funcionario funcionario = funcionarioRepository.findByEmail(loginRequest.email());
        if (funcionario != null && passwordEncoder.matches(loginRequest.senha(), funcionario.getSenha())) {
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.status(401).body("Email ou senha invalidos!");
        }
    }
}
