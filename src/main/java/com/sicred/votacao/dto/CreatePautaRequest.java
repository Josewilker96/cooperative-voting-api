package com.sicred.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePautaRequest {

    @NotBlank(message = "titulo não pode ser vazio")
    private String titulo;
}
