package br.com.ctech.pocclientes.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String tratarIllegalArgumentException(IllegalArgumentException ex,
                                                 RedirectAttributes redirectAttributes) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        return "redirect:/clientes";
    }

    @ExceptionHandler(DataAccessException.class)
    public String tratarDataAccessException(DataAccessException ex, Model model) {
        log.error("Erro de acesso a dados", ex);
        model.addAttribute("mensagemErro", "Não foi possível acessar os dados da aplicação no momento.");
        return "erro";
    }

    @ExceptionHandler(Exception.class)
    public String tratarExceptionGenerica(Exception ex, Model model) {
        log.error("Erro interno não tratado", ex);
        model.addAttribute("mensagemErro", "Ocorreu um erro interno ao processar a solicitação.");
        return "erro";
    }
}