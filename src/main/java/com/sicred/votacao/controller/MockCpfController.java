package com.sicred.votacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class MockCpfController {

    @GetMapping("/users/{cpf}")
    public ResponseEntity<?> validarCpf(@PathVariable String cpf) {
        if ("98765432100".equals(cpf)) {
            return ResponseEntity.ok(Map.of("status", "UNABLE_TO_VOTE"));
        }
        if ("11111111111".equals(cpf)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", "ABLE_TO_VOTE"));
    }
}