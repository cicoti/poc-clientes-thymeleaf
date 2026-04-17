package br.com.ctech.pocclientes.config;

import br.com.ctech.pocclientes.entity.Usuario;
import br.com.ctech.pocclientes.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login",
                    "/cadastro",
                    "/cadastro_login.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/menu", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
            	    .logoutUrl("/logout")
            	    .logoutSuccessUrl("/login?logout=true")
            	    .invalidateHttpSession(true)
            	    .deleteCookies("JSESSIONID")
            	    .permitAll()
            	)
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

            UserDetails userDetails = User.withUsername(usuario.getUsername())
                    .password(usuario.getPassword())
                    .roles("USER")
                    .build();

            return userDetails;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}