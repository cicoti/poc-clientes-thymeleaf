package br.com.ctech.pocclientes.service;

import br.com.ctech.pocclientes.entity.AcaoAuditoria;
import br.com.ctech.pocclientes.entity.Auditoria;
import br.com.ctech.pocclientes.repository.AuditoriaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, ObjectMapper objectMapper) {
        this.auditoriaRepository = auditoriaRepository;
        this.objectMapper = objectMapper;
    }

    public void registrar(String entidade, Long registroId, AcaoAuditoria acao, Object dadosAntes, Object dadosDepois) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(obterUsuarioAutenticado());
        auditoria.setDataHora(LocalDateTime.now());
        auditoria.setAcao(acao);
        auditoria.setEntidade(entidade);
        auditoria.setRegistroId(registroId);
        auditoria.setDadosAntes(serializar(dadosAntes));
        auditoria.setDadosDepois(serializar(dadosDepois));

        auditoriaRepository.save(auditoria);
    }

    private String obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Usuário autenticado não encontrado para auditoria.");
        }

        return authentication.getName();
    }

    private String serializar(Object objeto) {
        if (objeto == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar dados da auditoria.", e);
        }
    }
}