package com.sicred.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 5, max = 255, message = "Título deve ter entre 5 e 255 caracteres")
    private String titulo;
}
