package com.sicred.votacao.integration;

import com.sicred.votacao.exception.AssociadoNaoHabilitadoException;
import com.sicred.votacao.exception.CpfInvalidoException;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("!prod")
public class VoterEligibilityClientMock implements VoterEligibilityClient { // ← faltava isso aqui

    @Override
    public UserInfoResponse checkCpf(String cpf) {
        if ("98765432100".equals(cpf)) {
            throw new AssociadoNaoHabilitadoException("Associado não habilitado para votar");
        }

        if ("11111111111".equals(cpf)) {
            throw new CpfInvalidoException("CPF inválido");
        }

        if (cpf != null && cpf.matches("\\d{11}")) {
            return UserInfoResponse.builder().status("ABLE_TO_VOTE").build();
        }

        throw new CpfInvalidoException("CPF inválido");
    }
}