package br.com.ctech.pocclientes.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index.html"})
    public String inicio(Authentication authentication) {
        if (autenticado(authentication)) {
            return "redirect:/menu";
        }

        return "redirect:/login";
    }

    private boolean autenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}