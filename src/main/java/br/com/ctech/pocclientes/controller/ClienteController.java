package br.com.ctech.pocclientes.controller;

import br.com.ctech.pocclientes.entity.Cliente;
import br.com.ctech.pocclientes.entity.Endereco;
import br.com.ctech.pocclientes.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEndereco(new Endereco());

        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(Cliente cliente) {
        clienteService.salvar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        if (cliente.getEndereco() == null) {
            cliente.setEndereco(new Endereco());
        }

        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        clienteService.excluirPorId(id);
        return "redirect:/clientes";
    }
}