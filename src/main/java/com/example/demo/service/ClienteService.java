package com.example.demo.service;

import com.example.demo.DTO.ClienteDTO;
import com.example.demo.exception.ClienteException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.repository.ClienteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationTypeNotFoundException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Tag(name = "cliente", description = "Fornece serviços web REST para acesso e manipualação de Clientes.")
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService;

    public void salvar(Cliente cliente) {
        cliente.setUltimaAlteracao(ZonedDateTime.now().toLocalDateTime());
        clienteRepository.save(cliente);
    }

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
        editado.setSaldoDebito(cliente.getSaldoDebito());
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

    public List<Cliente> findClientesAniversariantesHoje() {
        LocalDate hoje = LocalDate.now();
        return clienteRepository.findClientesAniversariantes(hoje.getMonthValue(), hoje.getDayOfMonth());
    }

}
