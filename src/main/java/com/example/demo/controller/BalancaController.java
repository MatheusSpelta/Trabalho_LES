package com.example.demo.controller;

import com.example.demo.service.BalancaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balanca")
@AllArgsConstructor
public class BalancaController {

    private final BalancaService balancaService;

    @PostMapping("/peso/{porta}")
    public ResponseEntity<Double> getUltimoPeso(@PathVariable String porta) {

        // Aguarda até que o peso seja atualizado ou atinja timeout (ex: 2 segundos)
        Double peso = 0.0;
        long start = System.currentTimeMillis();
        while (peso == 0.0 && System.currentTimeMillis() - start < 2000) {
            peso = balancaService.getUltimoPeso();
            try {
                Thread.sleep(100); // Espera 100ms antes de tentar novamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (peso == 0.0) {
            throw new RuntimeException("Nenhum peso foi lido ainda.");
        }
        // Não feche a porta aqui, deixe aberta para próximas leituras
        return ResponseEntity.ok(peso);
    }

    @PostMapping("/close-port/{porta}")
    public ResponseEntity<String> closePort(@PathVariable String porta) {
        balancaService.closePort();
        return ResponseEntity.ok("Porta serial fechada");
    }

    @PostMapping("/open-port/{porta}")
    public ResponseEntity<String> openPort(@PathVariable String porta) {
        if (balancaService.init(porta)) {
            return ResponseEntity.ok("Porta serial aberta com sucesso");
        } else {
            return ResponseEntity.status(500).body("Falha ao abrir a porta serial");
        }
    }


}
