package br.com.ctech.pocclientes.repository;

import br.com.ctech.pocclientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}