package com.sicred.votacao.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenVotingSessionRequest {

    @Positive(message = "duracaoMinutos must be positive")
    private Integer duracaoMinutos;

}
