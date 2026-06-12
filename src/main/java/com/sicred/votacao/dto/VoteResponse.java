package com.sicred.votacao.dto;

import com.sicred.votacao.entity.TipoVoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {
    private Long id;
    private Long pautaId;
    private String identificadorAssociado;
    private TipoVoto voto;
    private LocalDateTime dataVoto;
}
