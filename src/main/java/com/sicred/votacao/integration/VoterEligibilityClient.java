package com.sicred.votacao.integration;

import com.sicred.votacao.integration.dto.UserInfoResponse;

public interface VoterEligibilityClient {
    UserInfoResponse checkCpf(String cpf);
}
