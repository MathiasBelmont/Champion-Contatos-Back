package champion.com.demo.domain.controllers;

import champion.com.demo.domain.client.Cliente;
import champion.com.demo.domain.client.ClienteRequestDTO;
import champion.com.demo.services.ClienteService;
import champion.com.demo.domain.user.Usuario;
import champion.com.demo.domain.repositories.ClienteRepository;
import champion.com.demo.domain.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import java.util.List;

@RestController
@RequestMapping("clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteService service;

    // 1. Agente cria contato
    @PostMapping
    public ResponseEntity criarContato(@RequestBody ClienteRequestDTO data) {
        Usuario agenteLogado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.criarCliente(data, agenteLogado); // Agora o Java sabe quem é o 'service'
        return ResponseEntity.ok().build();
    }

    // 2. Agente vê SEUS contatos
    @GetMapping("/meus")
    public ResponseEntity<List<Cliente>> listarMeusContatos() {
        Usuario agenteLogado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                service.getClientesPorAgente(agenteLogado.getId())
        );
    }

    //Gestor vê TODOS
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(service.getAllClientes());
    }

    // 3. Gestor vê contatos pendentes
    @GetMapping("/pendentes")
    public ResponseEntity<List<Cliente>> listarPendentes() {
        return ResponseEntity.ok(service.getPendentes());
    }

    // 4. Gestor aprova contato
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity aprovarContato(@PathVariable Long id) {
        service.aprovarCliente(id);
        return ResponseEntity.ok().build();
    }

    // 5. Gestor realoca contato para outro agente
    @PutMapping("/{id}/realocar/{agenteId}")
    @PreAuthorize("hasAuthority('GESTOR')")
    @Transactional
    public ResponseEntity realocarCliente(@PathVariable Long id, @PathVariable Long agenteId) {
        // 1. Busca o cliente no banco
        var clienteOptional = repository.findById(id);
        if (clienteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }

        // 2. Busca o novo agente
        var novoAgenteOptional = usuarioRepository.findById(agenteId);
        if (novoAgenteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Novo agente não encontrado.");
        }

        var cliente = clienteOptional.get();
        var novoAgente = novoAgenteOptional.get();

        // 3. Validação de Regra de Negócio: O novo usuário deve ser um AGENTE
        if (!novoAgente.getRole().name().equals("AGENTE")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O destino da realocação deve ser um Agente.");
        }

        // 4. Realiza a troca de "dono" do contato
        cliente.setAgente(novoAgente); 
        repository.save(cliente);

        return ResponseEntity.ok("Cliente realocado com sucesso para o agente: " + novoAgente.getNome());
    }

    // 6. Gestor vê contatos de um agente específico
    @GetMapping("/agente/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<List<Cliente>> listarClientesPorAgente(@PathVariable Long id) {
        // Usa o método que já existe no seu ClienteService
        return ResponseEntity.ok(service.getClientesPorAgente(id));
    }
}