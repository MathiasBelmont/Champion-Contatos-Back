package champion.com.demo.domain.client;

import champion.com.demo.domain.user.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "cliente") // Use o nome exato da tabela no banco
@Entity(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Nome", nullable = false)
    private String nome;

    // Mapeia o atributo 'email' para a coluna 'E_mail'
    @Column(name = "E_mail", unique = true, nullable = false)
    private String email;

    @Column(name = "Telefone")
    private String telefone;

    // Mapeia o atributo 'tipoContato' para a coluna 'Tipo_contato' que criámos no Passo 1
    @Column(name = "Tipo_contato")
    private String tipoContato;

    @Column(name = "Aprovado", nullable = false)
    private Boolean aprovado = false;

    @ManyToOne
    @JoinColumn(name = "Id_do_agente") // Mapeia para a chave estrangeira correta
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