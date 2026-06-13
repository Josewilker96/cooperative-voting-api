package com.sicred.votacao.dto.formulario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFormulario {
    private String tipo;
    private String label;
    private String valor;
}