package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ClienteDTO;
import com.example.demo.exception.ClienteException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.repository.ClienteRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;

@Service
@Tag(name = "cliente", description = "Fornece serviços web REST para acesso e manipualação de Clientes.")
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Transactional
    public Cliente saveAll(ClienteDTO clienteDTO) {
        Cliente cliente = clienteDTO.cliente();
        Endereco endereco = clienteDTO.endereco();

        validateCliente(cliente, null);

        Endereco end = enderecoService.saveAll(endereco);
        cliente.setEndereco(end);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente editId(UUID id, ClienteDTO clienteDTO) throws RelationTypeNotFoundException {
        Cliente cliente = clienteDTO.cliente();
        Endereco endereco = clienteDTO.endereco();

        Cliente editado = clienteRepository.findById(id)
                .orElseThrow(() -> new RelationTypeNotFoundException("Cliente com id " + id + " não encontrado."));

        validateCliente(cliente, id);

        enderecoService.editId(endereco.getId(), endereco);
        editado.setEndereco(endereco);

        editado.setNome(cliente.getNome());
        editado.setTelefone(cliente.getTelefone());
        editado.setAtivo(cliente.isAtivo());
        editado.setCpf(cliente.getCpf());
        editado.setCartao(cliente.getCartao());
        editado.setLimiteCredito(cliente.getLimiteCredito());
        editado.setMatricula(cliente.getMatricula());

        return clienteRepository.save(editado);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public void RecargaDebito(UUID id, double valor) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteException::clienteNaoEncontrado);

        cliente.setSaldoDebito(cliente.getSaldoDebito() + valor);

        clienteRepository.save(cliente);
    }

    public Cliente findById(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(ClienteException::clienteNaoEncontrado);
    }

    public void changeAtivo(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteException::clienteNaoEncontrado);

        cliente.setAtivo(!cliente.isAtivo());
        clienteRepository.save(cliente);
    }

    private void validateCliente(Cliente cliente, UUID id) {
        clienteRepository.findByMatricula(cliente.getMatricula())
                .ifPresent(existingCliente -> {
                    if (!existingCliente.getId().equals(id)) {
                        throw ClienteException.matriculaJaCadastrada();
                    }
                });

        clienteRepository.findByCpf(cliente.getCpf())
                .ifPresent(existingCliente -> {
                    if (!existingCliente.getId().equals(id)) {
                        throw ClienteException.cpfJaCadastrado();
                    }
                });

        clienteRepository.findByCartao(cliente.getCartao())
                .ifPresent(existingCliente -> {
                    if (!existingCliente.getId().equals(id)) {
                        throw ClienteException.cartaoJaCadastrado();
                    }
                });
    }

    public Cliente findByCartao(String cartao) {
        return clienteRepository.findByCartao(cartao)
                .orElseThrow(ClienteException::cartaoNaoEncontrado);
    }

}
