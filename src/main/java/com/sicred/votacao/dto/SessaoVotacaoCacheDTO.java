package com.sicred.votacao.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class SessaoVotacaoCacheDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long pautaId;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
}