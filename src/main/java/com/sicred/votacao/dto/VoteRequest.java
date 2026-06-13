package com.sicred.votacao.dto;

import com.sicred.votacao.entity.TipoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotBlank(message = "CPF é obrigatório")
    private String identificadorAssociado;

    @NotNull(message = "Voto é obrigatório")
    private TipoVoto voto; // SIM ou NAO
}