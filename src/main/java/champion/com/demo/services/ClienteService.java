package champion.com.demo.services;

import champion.com.demo.domain.client.Cliente;
import champion.com.demo.domain.client.ClienteRequestDTO;
import champion.com.demo.domain.repositories.ClienteRepository;
import champion.com.demo.domain.user.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    // Regra: cliente pertence ao agente logado
    public Cliente criarCliente(ClienteRequestDTO data, Usuario agente) {
        Cliente cliente = new Cliente(data, agente);
        return repository.save(cliente);
    }

    // REGRA 1: agente só vê os próprios clientes
    public List<Cliente> getClientesPorAgente(Long agenteId) {
        return repository.findByAgenteId(agenteId);
    }

    // REGRA 2: gestor pode ver todos
    public List<Cliente> getAllClientes() {
        return repository.findAll();
    }

    public List<Cliente> getPendentes() {
        return repository.findByAprovadoFalse();
    }

    public void aprovarCliente(Long id) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setAprovado(true);
        repository.save(cliente);
    }
}