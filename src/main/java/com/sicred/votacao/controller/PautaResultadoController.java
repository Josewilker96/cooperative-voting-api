package com.sicred.votacao.controller;

import com.sicred.votacao.dto.VotingResultResponse;
import com.sicred.votacao.dto.formulario.BotaoFormulario;
import com.sicred.votacao.dto.formulario.ItemFormulario;
import com.sicred.votacao.dto.formulario.TelaFormulario;
import com.sicred.votacao.exception.SessaoAindaAbertaException;
import com.sicred.votacao.service.VotingResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
public class PautaResultadoController {

    private final VotingResultService votingResultService;

    @Operation(summary = "Consulta o resultado da votação de uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Sessão ainda está aberta")
    })
    @GetMapping
    public ResponseEntity<TelaFormulario> resultado(@PathVariable Long pautaId) {
        try {
            VotingResultResponse result = votingResultService.apurarResultado(pautaId);

            TelaFormulario response = TelaFormulario.builder()
                    .tipo("RESULTADO")
                    .titulo("Resultado: " + result.getTitulo())
                    .itens(List.of(
                            ItemFormulario.builder()
                                    .tipo("TEXTO")
                                    .label("Total de Votos")
                                    .valor(String.valueOf(result.getTotalVotos()))
                                    .build(),
                            ItemFormulario.builder()
                                    .tipo("TEXTO")
                                    .label("Votos SIM")
                                    .valor(String.valueOf(result.getTotalSim()))
                                    .build(),
                            ItemFormulario.builder()
                                    .tipo("TEXTO")
                                    .label("Votos NÃO")
                                    .valor(String.valueOf(result.getTotalNao()))
                                    .build(),
                            ItemFormulario.builder()
                                    .tipo("TEXTO")
                                    .label("Resultado Final")
                                    .valor(result.getResultado())
                                    .build()
                    ))
                    .botaoOk(BotaoFormulario.builder()
                            .texto("Voltar")
                            .url("http://localhost:8080/api/v1/pautas")
                            .body(Map.of())
                            .build())
                    .build();

            return ResponseEntity.ok(response);
        } catch (SessaoAindaAbertaException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}