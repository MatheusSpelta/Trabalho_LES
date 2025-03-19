package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.FuncionarioDTO;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.model.Endereco;
import com.example.demo.model.Funcionario;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.security.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Funcionario", description = "Fornece serviços web REST para acesso e manipulação de dados de Funcionarios")
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Funcionario saveAll(FuncionarioDTO funcionarioDTO) {
        if (funcionarioRepository.findByEmail(funcionarioDTO.funcionario().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        if (funcionarioRepository.findByCpf(funcionarioDTO.funcionario().getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        Funcionario funcionario = funcionarioDTO.funcionario();
        Endereco endereco = funcionarioDTO.endereco();

        Endereco ende = enderecoService.saveAll(endereco);
        funcionario.setEndereco(ende);

        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario editId(UUID id, FuncionarioDTO funcionarioDTO) throws RelationTypeNotFoundException {
        Funcionario funcionario = funcionarioDTO.funcionario();
        Endereco endereco = funcionarioDTO.endereco();

        Funcionario editado = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Funcionario com id " + id + " não encontrado."));

        if (funcionarioRepository.findByEmail(funcionario.getEmail()).isPresent() && !funcionario.getId().equals(id)) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        if (funcionarioRepository.findByCpf(funcionario.getCpf()).isPresent() && !funcionario.getId().equals(id)) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        enderecoService.editId(endereco.getId(), endereco);
        editado.setEndereco(endereco);

        editado.setNome(funcionario.getNome());
        editado.setTelefone(funcionario.getTelefone());
        editado.setAtivo(funcionario.isAtivo());
        editado.setCpf(funcionario.getCpf());
        editado.setEmail(funcionario.getEmail());
        editado.setSenha(passwordEncoder.encode(funcionario.getSenha()));

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
        Funcionario funcionario = funcionarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha invalidos!"));
        if (passwordEncoder.matches(loginRequest.senha(), funcionario.getSenha())) {
            String token = jwtUtil.generateToken(funcionario.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Email ou senha invalidos!");
        }
    }
}
