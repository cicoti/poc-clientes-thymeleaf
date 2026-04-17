package br.com.ctech.pocclientes.controller;

import br.com.ctech.pocclientes.dto.CadastroLoginForm;
import br.com.ctech.pocclientes.service.UsuarioService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "expired", required = false) String expired,
            Authentication authentication,
            Model model
    ) {
        if (autenticado(authentication)) {
            return "redirect:/menu";
        }

        if (error != null) {
            model.addAttribute("mensagemErro", "Usuário ou senha inválidos.");
        }

        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Logout realizado com sucesso.");
        }

        if (success != null) {
            model.addAttribute("mensagemSucesso", "Usuário cadastrado com sucesso. Faça login.");
        }

        if (expired != null) {
            model.addAttribute("mensagemErro", "Sua sessão expirou. Faça login novamente.");
        }

        model.addAttribute("existeUsuarioCadastrado", usuarioService.existeUsuarioCadastrado());

        return "login";
    }

    @GetMapping({"/cadastro", "/cadastro_login.html"})
    public String cadastro(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CadastroLoginForm());
        }

        return "cadastro_login";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute("form") CadastroLoginForm form, Model model) {
        try {
            usuarioService.cadastrarUsuario(
                    form.getUsername(),
                    form.getEmail(),
                    form.getPassword(),
                    form.getConfirmacaoPassword()
            );

            return "redirect:/login?success=true";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            return "cadastro_login";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Não foi possível concluir o cadastro neste momento.");
            return "cadastro_login";
        }
    }

    private boolean autenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}