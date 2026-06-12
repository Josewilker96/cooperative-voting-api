package com.sicred.votacao.controller;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.VoteResponse;
import com.sicred.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    @Operation(summary = "Registra voto de associado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos / sessão encerrada"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Associado já votou")
    })
    @PostMapping
    public ResponseEntity<VoteResponse> registrarVoto(@Valid @RequestBody VoteRequest request) {
        VoteResponse response = votoService.registrarVoto(request);
        URI location = URI.create(String.format("/api/v1/votos/%d", response.getId()));
        return ResponseEntity.created(location).body(response);
    }
}
