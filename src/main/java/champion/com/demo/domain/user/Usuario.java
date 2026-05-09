package champion.com.demo.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Collection;
import java.util.List;

@Table(name = "USUARIO") // Nome exato da sua tabela no SQL
@Entity(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Nome", nullable = false)
    private String nome;

    // Mapeia o atributo "login" do Java para a coluna "Usuario" do Banco de Dados
    @Column(name = "Usuario", unique = true, nullable = false)
    private String login;

    @Column(name = "Senha", nullable = false)
    private String senha;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "Tipo_usuario", nullable = false)
    private UserRole role;

    // Construtor usado no Registo
    public Usuario(String login, String senha, UserRole role) {
        this.login = login;
        this.senha = senha;
        this.role = role;
        this.nome = "Utilizador Padrão"; // Pode alterar para receber o nome no RegisterDTO depois
    }

    // -- Métodos do UserDetails --

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.GESTOR || this.role == UserRole.GESTOR_TI) {
            return List.of(new SimpleGrantedAuthority("GESTOR"), new SimpleGrantedAuthority("AGENTE"));
        } else {
            return List.of(new SimpleGrantedAuthority("AGENTE"));
        }
    }

    @Override
    public String getPassword() { return senha; }

    @Override
    public String getUsername() { return login; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { 
        return this.ativo; 
    }
}