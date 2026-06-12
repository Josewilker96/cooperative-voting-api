package com.sicred.votacao.dto;

import com.sicred.votacao.entity.TipoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {

    @NotNull(message = "pautaId is required")
    private Long pautaId;

    @NotBlank(message = "identificadorAssociado is required")
    private String identificadorAssociado;

    @NotNull(message = "voto is required")
    private TipoVoto voto;
}
