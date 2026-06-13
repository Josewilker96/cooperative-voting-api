package com.sicred.votacao.controller;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    @Operation(summary = "Abre sessão de votação para uma pauta")
    @PostMapping
    public ResponseEntity<VotingSessionResponse> abrirSessao(@PathVariable Long pautaId,
                                                             @Valid @RequestBody(required = false) OpenVotingSessionRequest request) {
        VotingSessionResponse response = sessaoVotacaoService.abrirSessao(pautaId, request);
        URI location = URI.create(String.format("/api/v1/pautas/%d/sessao/%d", pautaId, response.getId()));
        return ResponseEntity.created(location).body(response);
    }
}