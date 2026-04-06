package champion.com.demo.domain.controllers;

import champion.com.demo.domain.client.Cliente;
import champion.com.demo.domain.client.ClienteRequestDTO;
import champion.com.demo.domain.user.Usuario;
import champion.com.demo.domain.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 1. Agente cria contato
    @PostMapping
    public ResponseEntity criarContato(@RequestBody ClienteRequestDTO data) {
        Usuario agenteLogado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.criarCliente(data, agenteLogado);
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

    //5. Cadastra cliente em massa
    @PostMapping(value = "/lote", consumes = "multipart/form-data")
    public ResponseEntity uploadClientes(@RequestParam("file") MultipartFile file) {
        try {
            Usuario agenteLogado = (Usuario) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream())
            );

            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {

                // Pula cabeçalho
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] dados = linha.split(",");

                ClienteRequestDTO dto = new ClienteRequestDTO();
                dto.setNome(dados[0]);
                dto.setEmail(dados[1]);
                dto.setTelefone(dados[2]);

                Cliente cliente = new Cliente(dto, agenteLogado);
                repository.save(cliente);
            }

            return ResponseEntity.ok("Upload realizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar arquivo: " + e.getMessage());
        }
    }
}