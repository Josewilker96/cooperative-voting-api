package com.sicred.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PautaResponse {
    private Long id;
    private String titulo;
    private LocalDateTime dataCriacao;
}
