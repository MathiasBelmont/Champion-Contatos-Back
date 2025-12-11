package main.java.champion.com.demo.domain.repositories;

import main.java.champion.com.demo.domain.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O Spring Security usa isso para buscar o usuário pelo login
    UserDetails findByLogin(String login);
}