package br.com.ctech.pocclientes.service;

import br.com.ctech.pocclientes.entity.Usuario;
import br.com.ctech.pocclientes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existeUsuarioCadastrado() {
        return usuarioRepository.count() > 0;
    }

    @Transactional
    public Usuario cadastrarUsuario(String username, String email, String password, String confirmacaoPassword) {
        String usernameTratado = tratar(username);
        String emailTratado = tratar(email);
        String passwordTratado = tratar(password);
        String confirmacaoTratada = tratar(confirmacaoPassword);

        if (usernameTratado == null) {
            throw new IllegalArgumentException("Informe o usuário.");
        }

        if (emailTratado == null) {
            throw new IllegalArgumentException("Informe o email.");
        }

        if (passwordTratado == null) {
            throw new IllegalArgumentException("Informe a senha.");
        }

        if (confirmacaoTratada == null) {
            throw new IllegalArgumentException("Confirme a senha.");
        }

        if (!passwordTratado.equals(confirmacaoTratada)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (usuarioRepository.existsByUsername(usernameTratado)) {
            throw new IllegalArgumentException("Já existe um usuário com esse login.");
        }

        if (usuarioRepository.existsByEmail(emailTratado)) {
            throw new IllegalArgumentException("Já existe um usuário com esse email.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameTratado);
        usuario.setEmail(emailTratado);
        usuario.setPassword(passwordEncoder.encode(passwordTratado));

        return usuarioRepository.save(usuario);
    }

    private String tratar(String valor) {
        if (valor == null) {
            return null;
        }

        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }
}