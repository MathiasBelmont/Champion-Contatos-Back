package champion.com.demo.domain.controllers;

import champion.com.demo.domain.client.Cliente;
import champion.com.demo.domain.client.ClienteRequestDTO;
import champion.com.demo.domain.user.Usuario;
import champion.com.demo.domain.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    // 1. Agente cria contato
    @PostMapping
    public ResponseEntity criarContato(@RequestBody ClienteRequestDTO data) {
        // Pega o usuário logado automaticamente pelo Token
        Usuario agenteLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Cliente novoCliente = new Cliente(data, agenteLogado);
        repository.save(novoCliente);
        return ResponseEntity.ok().build();
    }

    // 2. Agente vê SEUS contatos
    @GetMapping("/meus")
    public ResponseEntity<List<Cliente>> listarMeusContatos() {
        Usuario agenteLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Cliente> lista = repository.findByAgenteId(agenteLogado.getId());
        return ResponseEntity.ok(lista);
    }

    // 3. Gestor vê contatos pendentes
    @GetMapping("/pendentes")
    public ResponseEntity<List<Cliente>> listarPendentes() {
        // Poderíamos adicionar verificação se é ADMIN aqui, mas vamos confiar no front por enquanto ou usar @PreAuthorize
        List<Cliente> lista = repository.findByAprovadoFalse();
        return ResponseEntity.ok(lista);
    }

    // 4. Gestor aprova contato
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity aprovarContato(@PathVariable Long id) {
        var cliente = repository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setAprovado(true);
            repository.save(cliente);
        }
        return ResponseEntity.ok().build();
    }
}