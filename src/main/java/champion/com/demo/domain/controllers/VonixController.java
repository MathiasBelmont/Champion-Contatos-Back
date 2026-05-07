package champion.com.demo.domain.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/vonix")
public class VonixController {

    // O Token fica protegido aqui no servidor!
    private final String VONIX_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21lcklkIjoiY2hhbXBpb25zYSIsImFwcGxpY2F0aW9uSWQiOiJzaXN0ZW1hY2FydGVpcmEiLCJhcHBsaWNhdGlvbk5hbWUiOiJzaXN0ZW1hX2NhcnRlaXJhIiwidG9rZW5LZXkiOiI5ZmVkYWVmZi01MTcwLTU1NGEtODVkNS00ZjRkNWZmZjFmNjMiLCJpYXQiOjE3NzgxNzksImV4cCI6MzE1NTg3NzMzNzl9.jOu8nLRyGQYi1SPJQfE4HX_6kV_WtCNT86fW86mJdcI";
    private final String VONIX_BASE_URL = "https://championsa.api.vonixcc.com.br";

    @PostMapping("/discar")
    public ResponseEntity<?> discarRamal(@RequestBody DiscarRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Monta a URL com o ID do agente recebido do React
            String url = VONIX_BASE_URL + "/agent/" + request.agentId() + "/dial";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + VONIX_TOKEN);
            headers.set("Content-Type", "application/json");

            // Monta o corpo da requisição para a Vonix
            Map<String, String> body = new HashMap<>();
            body.put("number", request.numero());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // Dispara a requisição para a Vonix
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao conectar com a Vonix: " + e.getMessage());
        }
    }
}

// Classe auxiliar para receber os dados do React
record DiscarRequest(String numero, String agentId) {}