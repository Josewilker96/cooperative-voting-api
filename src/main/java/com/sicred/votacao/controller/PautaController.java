package com.sicred.votacao.controller;

import com.sicred.votacao.dto.CreatePautaRequest;
import com.sicred.votacao.dto.PautaResponse;
import com.sicred.votacao.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @Operation(summary = "Cria uma nova pauta")
    @PostMapping
    public ResponseEntity<PautaResponse> createPauta(@Valid @RequestBody CreatePautaRequest request) {
        PautaResponse created = pautaService.createPauta(request);
        URI location = URI.create(String.format("/api/v1/pautas/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }
}
