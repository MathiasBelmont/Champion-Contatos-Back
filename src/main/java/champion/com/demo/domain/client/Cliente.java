package champion.com.demo.domain.client;

import champion.com.demo.domain.user.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "clientes")
@Entity(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String tipoContato;
    private Boolean aprovado = false;

    @ManyToOne
    @JoinColumn(name = "agente_id")
    private Usuario agente;

    // DTO para facilitar a criação
    public Cliente(ClienteRequestDTO data, Usuario agente) {
        this.nome = data.nome();
        this.email = data.email();
        this.telefone = data.telefone();
        this.tipoContato = data.tipoContato();
        this.agente = agente;
        this.aprovado = false; // Sempre começa não aprovado
    }
}