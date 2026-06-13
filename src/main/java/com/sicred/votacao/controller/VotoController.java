package com.sicred.votacao.controller;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.formulario.TelaFormulario;
import com.sicred.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    @Operation(summary = "Valida voto e retorna tela de confirmação ou erro")
    @PostMapping
    public ResponseEntity<TelaFormulario> votar(@PathVariable Long pautaId,
                                                @Valid @RequestBody VoteRequest request) {
        TelaFormulario tela = votoService.processarVoto(pautaId, request);
        return ResponseEntity.ok(tela);
    }

    @Operation(summary = "Registra voto após confirmação")
    @PostMapping("/registrar")
    public ResponseEntity<Void> registrarVoto(@PathVariable Long pautaId,
                                              @Valid @RequestBody VoteRequest request) {
        votoService.registrarVoto(pautaId, request);
        return ResponseEntity.status(201).build();
    }
}