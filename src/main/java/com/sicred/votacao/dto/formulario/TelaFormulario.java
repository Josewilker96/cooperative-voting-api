package com.sicred.votacao.dto.formulario;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelaFormulario {
    private String tipo;
    private String titulo;
    private List<ItemFormulario> itens;
    private BotaoFormulario botaoOk;
    private BotaoFormulario botaoCancelar;
}