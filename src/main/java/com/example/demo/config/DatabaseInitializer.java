package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.DTO.FuncionarioDTO;
import com.example.demo.model.Endereco;
import com.example.demo.model.Funcionario;
import com.example.demo.model.InterfacePermissao;
import com.example.demo.model.TipoPermissao;
import com.example.demo.service.EnderecoService;
import com.example.demo.service.FuncionarioService;
import com.example.demo.service.InterfacePermissaoService;
import com.example.demo.service.TipoPermissaoService;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(
            TipoPermissaoService tipoPermissaoService,
            InterfacePermissaoService interfacePermissaoService,
            FuncionarioService funcionarioService,
            EnderecoService enderecoService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Inicializa permissões
            List<String> permissoes = Arrays.asList("Criar", "Editar", "Excluir", "Visualizar");
            for (String descricao : permissoes) {
                if (!tipoPermissaoService.existsByDescricao(descricao)) {
                    TipoPermissao tipoPermissao = new TipoPermissao();
                    tipoPermissao.setDescricao(descricao);
                    tipoPermissao.setAtivo(true); // Exemplo de outro atributo, ajuste conforme necessário
                    tipoPermissaoService.salvar(tipoPermissao);
                }
            }

            // Inicializa interfaces
            List<String> interfaces = Arrays.asList("Funcionario", "Fornecedor", "Produto", "Venda", "Cliente");
            for (String descricao : interfaces) {
                if (!interfacePermissaoService.existsByDescricao(descricao)) {
                    InterfacePermissao interfacePermissao = new InterfacePermissao();
                    interfacePermissao.setDescricao(descricao);
                    interfacePermissaoService.salvar(interfacePermissao);
                }
            }

            // Cria um funcionário padrão
            if (funcionarioService.findAll().isEmpty()) {
                Endereco endereco = new Endereco();
                endereco.setRua("Rua Padrão");
                endereco.setNumero("123");
                endereco.setCidade("Cidade Padrão");
                endereco.setBairro("Bairro Padrão");
                endereco.setCep("00000-000");

                Funcionario funcionario = new Funcionario();
                funcionario.setNome("Administrador");
                funcionario.setCpf("000.000.000-00");
                funcionario.setEmail("admin@example.com");
                funcionario.setSenha("admin123");
                funcionario.setTelefone("123456789");
                funcionario.setEndereco(endereco);

                // Usa o método saveAll do FuncionarioService
                funcionarioService.saveAll(new FuncionarioDTO(funcionario, endereco, List.of()));
            }
        };
    }
}