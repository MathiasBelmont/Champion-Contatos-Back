package champion.com.demo.domain.repositories;

import champion.com.demo.domain.client.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Busca clientes de um agente específico
    List<Cliente> findByAgenteId(Long agenteId);
    
    // Busca clientes que ainda não foram aprovados (para o Gestor)
    List<Cliente> findByAprovadoFalse();
}