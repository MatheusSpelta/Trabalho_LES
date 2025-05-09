package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.DTO.ClienteDTO;
import com.example.demo.DTO.FuncionarioDTO;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.model.Funcionario;
import com.example.demo.model.InterfacePermissao;
import com.example.demo.model.Produto;
import com.example.demo.model.TipoPermissao;
import com.example.demo.service.ClienteService;
import com.example.demo.service.EnderecoService;
import com.example.demo.service.FuncionarioService;
import com.example.demo.service.InterfacePermissaoService;
import com.example.demo.service.ProdutoService;
import com.example.demo.service.TipoPermissaoService;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(
            TipoPermissaoService tipoPermissaoService,
            InterfacePermissaoService interfacePermissaoService,
            FuncionarioService funcionarioService,
            EnderecoService enderecoService,
            ClienteService clienteService,
            ProdutoService produtoService,
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
            // Cria um cliente padrão
            if (clienteService.findAll().isEmpty()) {
                Endereco enderecoCliente = new Endereco();
                enderecoCliente.setRua("Rua Cliente");
                enderecoCliente.setNumero("456");
                enderecoCliente.setCidade("Cidade Cliente");
                enderecoCliente.setBairro("Bairro Cliente");
                enderecoCliente.setCep("11111-111");

                Cliente cliente = new Cliente();
                cliente.setNome("Cliente Teste");
                cliente.setCpf("123.456.789-00");
                cliente.setCartao("1234");
                cliente.setSaldoDebito(500.0);
                cliente.setLimiteCredito(1000.0);
                cliente.setTelefone("11999999999");
                cliente.setEndereco(enderecoCliente);

                // Usa o método saveAll do ClienteService
                clienteService.saveAll(new ClienteDTO(cliente, enderecoCliente));
            }

            // Cria dois produtos padrão
            if (produtoService.findAll().isEmpty()) {
                Produto produto1 = new Produto();
                produto1.setDescricao("Produto 1");
                produto1.setEAN("1111111111111");
                produto1.setCodigo("P001");
                produto1.setValorVenda(50.0);
                produto1.setValorCusto(30.0);
                produto1.setAtivo(true);

                Produto produto2 = new Produto();
                produto2.setDescricao("Produto 2");
                produto2.setEAN("2222222222222");
                produto2.setCodigo("P002");
                produto2.setValorVenda(100.0);
                produto2.setValorCusto(70.0);
                produto2.setAtivo(true);

                // Salva os produtos
                produtoService.saveAll(produto1);
                produtoService.saveAll(produto2);
            }
        };
    }
}