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
import com.example.demo.DTO.LoginResponse;
import com.example.demo.exception.FuncionarioException;
import com.example.demo.exception.PermissaoException;
import com.example.demo.model.Endereco;
import com.example.demo.model.Funcionario;
import com.example.demo.model.InterfacePermissao;
import com.example.demo.model.Permissao;
import com.example.demo.model.TipoPermissao;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.security.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;

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

    @Autowired
    private TipoPermissaoService tipoPermissaoService;

    @Autowired
    private PermissaoService permissaoService;

    @Autowired
    private InterfacePermissaoService interfacePermissaoService;

    @Transactional
    public Funcionario saveAll(FuncionarioDTO funcionarioDTO) {
        try {
            if (funcionarioRepository.findByEmailIgnoreCase(funcionarioDTO.funcionario().getEmail()).isPresent()) {
                throw FuncionarioException.emailJaCadastrado();
            }
            if (funcionarioRepository.findByCpf(funcionarioDTO.funcionario().getCpf()).isPresent()) {
                throw FuncionarioException.cpfJaCadastrado();
            }

            Funcionario funcionario = funcionarioDTO.funcionario();
            Endereco endereco = funcionarioDTO.endereco();

            Endereco ende = enderecoService.saveAll(endereco);
            funcionario.setEndereco(ende);

            funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
            Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
            criarPermissoesParaFuncionario(funcionarioSalvo, funcionarioDTO.permissoes());

            return funcionarioRepository.save(funcionarioSalvo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o funcionário: " + e.getMessage(), e);
        }
    }

    private void criarPermissoesParaFuncionario(Funcionario funcionario, List<Permissao> permissoes) {
        List<TipoPermissao> tiposPermissao = tipoPermissaoService.findAll();
        List<InterfacePermissao> interfacesPermissao = interfacePermissaoService.findAll();

        for (TipoPermissao tipo : tiposPermissao) {
            for (InterfacePermissao interfacePermissao : interfacesPermissao) {
                Permissao permissao = new Permissao();
                permissao.setDescricao(
                        "Permissão para " + tipo.getDescricao() + " em " + interfacePermissao.getDescricao());
                permissao.setFuncionario(funcionario);
                permissao.setTipoPermissao(tipo);
                permissao.setInterfacePermissao(interfacePermissao);
                permissao.setAtivo(false); // Inicialmente todas as permissões são inativas
                permissaoService.salvar(permissao);
            }
        }
        for (Permissao permissao : permissoes) {
            permissaoService.salvar(ajustarPermissao(funcionario, permissao));
        }

    }

    private Permissao ajustarPermissao(Funcionario funcionario, Permissao permissaoDTO) {
        Permissao permissao = permissaoService.findByFuncionarioAndTipoPermissaoAndInterfacePermissao(
                funcionario.getId(),
                permissaoDTO.getTipoPermissao().getId(),
                permissaoDTO.getInterfacePermissao().getId())
                .orElseThrow(PermissaoException::permissaoNaoEncotnrada); // Usando PermissaoException personalizada

        permissao.setAtivo(permissaoDTO.isAtivo());
        return permissaoService.editarId(permissao.getId(), permissao);
    }

    public Funcionario editId(UUID id, FuncionarioDTO funcionarioDTO) throws RelationTypeNotFoundException {
        Funcionario funcionario = funcionarioDTO.funcionario();
        Endereco endereco = funcionarioDTO.endereco();

        Funcionario editado = funcionarioRepository.findById(id)
                .orElseThrow(FuncionarioException::funcionarioNaoEncontrado);

        if (funcionarioRepository.findByEmailIgnoreCase(funcionario.getEmail()).isPresent()
                && !funcionario.getId().equals(id)) {
            throw FuncionarioException.emailJaCadastrado();
        }
        if (funcionarioRepository.findByCpf(funcionario.getCpf()).isPresent() && !funcionario.getId().equals(id)) {
            throw FuncionarioException.cpfJaCadastrado();
        }

        enderecoService.editId(endereco.getId(), endereco);
        editado.setEndereco(endereco);

        editado.setNome(funcionario.getNome());
        editado.setTelefone(funcionario.getTelefone());
        editado.setAtivo(funcionario.isAtivo());
        editado.setCpf(funcionario.getCpf());
        editado.setEmail(funcionario.getEmail());
        editado.setSenha(passwordEncoder.encode(funcionario.getSenha()));

        atualizarPermissoesDoFuncionario(editado, funcionarioDTO.permissoes());

        return funcionarioRepository.save(editado);
    }

    private void atualizarPermissoesDoFuncionario(Funcionario funcionario, List<Permissao> permissoesDTO) {
        // Busca todas as permissões existentes do funcionário
        List<Permissao> permissoesExistentes = permissaoService.findByFuncionario(funcionario.getId());

        // Inicializa todas as permissões como inativas
        for (Permissao permissao : permissoesExistentes) {
            permissao.setAtivo(false);
            permissaoService.editarId(permissao.getId(), permissao);
        }

        // Atualiza as permissões que estão marcadas como ativas no DTO
        for (Permissao permissaoDTO : permissoesDTO) {
            permissaoService.salvar(ajustarPermissao(funcionario, permissaoDTO));
        }
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(UUID id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(FuncionarioException::funcionarioNaoEncontrado);
    }

    public void changeAtivo(UUID id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(FuncionarioException::funcionarioNaoEncontrado);

        funcionario.setAtivo(!funcionario.isAtivo());

        funcionarioRepository.save(funcionario);
    }

    public void changePassword(UUID id, String senha) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(FuncionarioException::funcionarioNaoEncontrado);

        funcionario.setSenha(passwordEncoder.encode(senha));
        funcionarioRepository.save(funcionario);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        // Busca o funcionário pelo email
        Funcionario funcionario = funcionarioRepository.findByEmailIgnoreCase(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos!"));

        // Verifica se a senha está correta
        if (!passwordEncoder.matches(loginRequest.senha(), funcionario.getSenha())) {
            return ResponseEntity.status(401).body(null);
        }

        // Gera o token JWT
        String token = jwtUtil.generateToken(funcionario.getEmail());

        // Busca as permissões do funcionário
        List<Permissao> permissoes = permissaoService.findByFuncionario(funcionario.getId());

        // Retorna o token, o funcionário e as permissões no LoginResponse
        return ResponseEntity.ok(new LoginResponse(token, funcionario, permissoes));
    }

    public Funcionario getFuncionarioLogado(String token) {
        // Extrai o nome de usuário (email) do token
        String email = jwtUtil.extractUsername(token);

        // Busca o funcionário no banco de dados pelo email
        return funcionarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(FuncionarioException::funcionarioNaoEncontrado); // Lança exceção se não encontrar
    }
}
