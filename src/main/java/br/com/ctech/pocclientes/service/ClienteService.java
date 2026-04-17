package br.com.ctech.pocclientes.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ctech.pocclientes.entity.AcaoAuditoria;
import br.com.ctech.pocclientes.entity.Cliente;
import br.com.ctech.pocclientes.entity.Endereco;
import br.com.ctech.pocclientes.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        boolean novoCliente = cliente.getId() == null;
        Map<String, Object> dadosAntes = null;

        if (!novoCliente) {
            Cliente clienteExistente = clienteRepository.findById(cliente.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + cliente.getId()));

            dadosAntes = criarSnapshotCliente(clienteExistente);

            if (isBlank(cliente.getCodigoCliente())) {
                cliente.setCodigoCliente(clienteExistente.getCodigoCliente());
            }

            if (cliente.getEndereco() != null
                    && clienteExistente.getEndereco() != null
                    && isBlank(cliente.getEndereco().getCodigo())) {
                cliente.getEndereco().setCodigo(clienteExistente.getEndereco().getCodigo());
            }
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
            clienteSalvo = clienteRepository.saveAndFlush(clienteSalvo);
        }

        Map<String, Object> dadosDepois = criarSnapshotCliente(clienteSalvo);

        auditoriaService.registrar(
                "CLIENTE",
                clienteSalvo.getId(),
                novoCliente ? AcaoAuditoria.CREATE : AcaoAuditoria.UPDATE,
                dadosAntes,
                dadosDepois
        );

        return clienteSalvo;
    }

    @Transactional
    public void excluirPorId(Long id) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        Map<String, Object> dadosAntes = criarSnapshotCliente(clienteExistente);

        clienteRepository.delete(clienteExistente);
        clienteRepository.flush();

        auditoriaService.registrar(
                "CLIENTE",
                id,
                AcaoAuditoria.DELETE,
                dadosAntes,
                null
        );
    }

    private Map<String, Object> criarSnapshotCliente(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", cliente.getId());
        snapshot.put("codigoCliente", cliente.getCodigoCliente());
        snapshot.put("nome", cliente.getNome());
        snapshot.put("email", cliente.getEmail());
        snapshot.put("telefone", cliente.getTelefone());
        snapshot.put("endereco", criarSnapshotEndereco(cliente.getEndereco()));

        return snapshot;
    }

    private Map<String, Object> criarSnapshotEndereco(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", endereco.getId());
        snapshot.put("codigo", endereco.getCodigo());
        snapshot.put("logradouro", endereco.getLogradouro());
        snapshot.put("numero", endereco.getNumero());
        snapshot.put("complemento", endereco.getComplemento());
        snapshot.put("bairro", endereco.getBairro());
        snapshot.put("cep", endereco.getCep());
        snapshot.put("cidade", endereco.getCidade());
        snapshot.put("estado", endereco.getEstado());

        return snapshot;
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