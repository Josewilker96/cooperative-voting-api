package com.sicred.votacao.controller;

import com.sicred.votacao.dto.VotingResultResponse;
import com.sicred.votacao.service.VotingResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
public class PautaResultadoController {

    private final VotingResultService votingResultService;

    @Operation(summary = "Consulta o resultado da votação de uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada")
    })
    @GetMapping
    public ResponseEntity<VotingResultResponse> resultado(@PathVariable Long pautaId) {
        VotingResultResponse response = votingResultService.apurarResultado(pautaId);
        return ResponseEntity.ok(response);
    }
}
