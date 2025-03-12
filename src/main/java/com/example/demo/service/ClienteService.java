package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import javax.management.relation.RelationTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;


import io.swagger.v3.oas.annotations.tags.Tag;


@Service
@Tag(name = "cliente", description = "Fornece serviços web REST para acesso e manipualação de Clientes.")
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente saveAll(Cliente cliente) {
        
        validateCliente(cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente editId(UUID id, Cliente cliente) throws RelationTypeNotFoundException {
        Cliente editado = clienteRepository.findById(id)
           .orElseThrow(() -> new RelationTypeNotFoundException("Cliente com id " + id + " não encontrado."));

           validateCliente(cliente, id);

            editado.setNome(cliente.getNome());
            editado.setTelefone(cliente.getTelefone());
            editado.setAtivo(cliente.isAtivo());
            editado.setCpf(cliente.getCpf());
            editado.setEndereco(cliente.getEndereco());
            editado.setCartao(cliente.getCartao());
            editado.setLimiteCredito(cliente.getLimiteCredito());
            editado.setMatricula(cliente.getMatricula());

            return clienteRepository.save(editado);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public void RecargaDebito(UUID id, double valor) throws RelationTypeNotFoundException {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Cliente com id " + id + " não encontrado."));

        cliente.setSaldoDebito(cliente.getSaldoDebito() + valor);

        clienteRepository.save(cliente);
    }

    public Cliente findById(UUID id) throws RelationTypeNotFoundException {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Cliente com id " + id + " não encontrado."));
    }

    public void changeAtivo(UUID id) throws RelationTypeNotFoundException {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RelationTypeNotFoundException("Cliente com id " + id + " não encontrado."));

        cliente.setAtivo(!cliente.isAtivo());
        clienteRepository.save(cliente);
    }

    private void validateCliente(Cliente cliente) {
        validateCliente(cliente, null);
    }

    private void validateCliente(Cliente cliente, UUID id) {
        clienteRepository.findByMatricula(cliente.getMatricula())
            .ifPresent(existingCliente -> {
                if (!existingCliente.getId().equals(id)) {
                    throw new IllegalArgumentException("Já existe um cliente com a matrícula " + cliente.getMatricula());
                }
            });

        clienteRepository.findByCpf(cliente.getCpf())
            .ifPresent(existingCliente -> {
                if (!existingCliente.getId().equals(id)) {
                    throw new IllegalArgumentException("Já existe um cliente com o CPF " + cliente.getCpf());
                }
            });

        clienteRepository.findByCartao(cliente.getCartao())
            .ifPresent(existingCliente -> {
                if (!existingCliente.getId().equals(id)) {
                    throw new IllegalArgumentException("Já existe um cliente com o cartão " + cliente.getCartao());
                }
            });
    }
    
}
