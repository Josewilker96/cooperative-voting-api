package com.sicred.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingResultResponse {
    private Long pautaId;
    private String titulo;
    private long totalVotos;
    private long totalSim;
    private long totalNao;
    private String resultado; // APROVADA, REJEITADA, EMPATADA
}
