package br.com.ctech.pocclientes.service;

import br.com.ctech.pocclientes.entity.Cliente;
import br.com.ctech.pocclientes.entity.Endereco;
import br.com.ctech.pocclientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() != null) {
            clienteRepository.findById(cliente.getId())
                    .ifPresent(clienteExistente -> {
                        if (isBlank(cliente.getCodigoCliente())) {
                            cliente.setCodigoCliente(clienteExistente.getCodigoCliente());
                        }

                        if (cliente.getEndereco() != null
                                && clienteExistente.getEndereco() != null
                                && isBlank(cliente.getEndereco().getCodigo())) {
                            cliente.getEndereco().setCodigo(clienteExistente.getEndereco().getCodigo());
                        }
                    });
        }

        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setCliente(cliente);
        }

        Cliente clienteSalvo = clienteRepository.saveAndFlush(cliente);

        boolean precisaSalvarNovamente = false;

        if (isBlank(clienteSalvo.getCodigoCliente())) {
            clienteSalvo.setCodigoCliente(gerarCodigoCliente(clienteSalvo.getId()));
            precisaSalvarNovamente = true;
        }

        Endereco endereco = clienteSalvo.getEndereco();
        if (endereco != null && isBlank(endereco.getCodigo())) {
            endereco.setCodigo(gerarCodigoEndereco(endereco.getId()));
            precisaSalvarNovamente = true;
        }

        if (precisaSalvarNovamente) {
            clienteSalvo = clienteRepository.save(clienteSalvo);
        }

        return clienteSalvo;
    }

    public void excluirPorId(Long id) {
        clienteRepository.deleteById(id);
    }

    private String gerarCodigoCliente(Long id) {
        return String.format("CL%06d", id);
    }

    private String gerarCodigoEndereco(Long id) {
        return String.format("END%06d", id);
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}