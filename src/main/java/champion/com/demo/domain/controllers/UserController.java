package champion.com.demo.domain.controllers;

import champion.com.demo.domain.user.Usuario;
import champion.com.demo.domain.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("usuarios")
public class UserController {

    @Autowired
    private UsuarioRepository repository;

    /**
     * CAMADA DE PROTEÇÃO (RBAC):
     * A anotação @PreAuthorize garante que o Spring Security intercepte a requisição,
     * valide o JWT e bloqueie automaticamente qualquer usuário que não tenha o papel de GESTOR.
     */

    // 1. Listar todos os usuários
    @GetMapping
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> lista = repository.findAll();
        return ResponseEntity.ok(lista);
    }

    // 2. Buscar um usuário específico
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = repository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
    }

    // 3. Criar um novo usuário (Agente ou Gestor)
    @PostMapping
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity criarUsuario(@RequestBody Usuario novoUsuario) {
        // Encriptar a senha antes de salvar no banco é fundamental para a segurança
        String encryptedPassword = new BCryptPasswordEncoder().encode(novoUsuario.getPassword());
        novoUsuario.setSenha(encryptedPassword); 
        
        repository.save(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 4. Atualizar dados do usuário
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosAtualizados) {
        Optional<Usuario> usuarioExistente = repository.findById(id);
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioExistente.get();
        usuario.setNome(dadosAtualizados.getNome());
        usuario.setRole(dadosAtualizados.getRole());
        
        // Só atualiza a senha se uma nova for enviada
        if (dadosAtualizados.getPassword() != null && !dadosAtualizados.getPassword().isEmpty()) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(dadosAtualizados.getPassword());
            usuario.setSenha(encryptedPassword);
        }

        repository.save(usuario);
        return ResponseEntity.ok("Usuário atualizado com sucesso.");
    }

    // 5. Deletar um usuário
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity deletarUsuario(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        repository.deleteById(id);
        return ResponseEntity.ok("Usuário deletado com sucesso.");
    }
}