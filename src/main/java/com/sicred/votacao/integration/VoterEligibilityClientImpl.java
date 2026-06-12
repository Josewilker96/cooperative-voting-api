package com.sicred.votacao.integration;

import com.sicred.votacao.exception.AssociadoNaoHabilitadoException;
import com.sicred.votacao.exception.CpfInvalidoException;
import com.sicred.votacao.exception.ServicoExternoIndisponivelException;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;

@Component
@RequiredArgsConstructor
public class VoterEligibilityClientImpl implements VoterEligibilityClient {

    private static final Logger log = LoggerFactory.getLogger(VoterEligibilityClientImpl.class);

    private final RestTemplate restTemplate;

    @Value("${integration.user-info.base-url}")
    private String baseUrl;

    public VoterEligibilityClientImpl(RestTemplateBuilder builder) {
        // configure timeouts (connect and read)
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    public UserInfoResponse checkCpf(String cpf) {
        log.info("Validating CPF with external service: {}", cpf);
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("users", cpf)
                .toUriString();
        try {
            ResponseEntity<UserInfoResponse> resp = restTemplate.getForEntity(url, UserInfoResponse.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                String status = resp.getBody().getStatus();
                log.info("External validation result for {}: {}", cpf, status);
                if ("ABLE_TO_VOTE".equalsIgnoreCase(status)) {
                    return resp.getBody();
                } else {
                    // user not able to vote
                    log.warn("Associado {} is NOT able to vote: {}", cpf, status);
                    throw new AssociadoNaoHabilitadoException("Associado não habilitado para votar");
                }
            } else {
                log.warn("Unexpected response from external service: {} -> {}", url, resp.getStatusCode());
                throw new ServicoExternoIndisponivelException("Serviço externo indisponível");
            }
        } catch (HttpClientErrorException.NotFound nfe) {
            log.warn("CPF inválido according to external service: {}", cpf);
            throw new CpfInvalidoException("CPF inválido");
        } catch (ResourceAccessException rae) {
            log.error("Timeout or network error when calling external service: {}", rae.getMessage());
            throw new ServicoExternoIndisponivelException("Serviço externo indisponível");
        } catch (Exception ex) {
            log.error("Error when calling external service: {}", ex.getMessage(), ex);
            throw new ServicoExternoIndisponivelException("Serviço externo indisponível");
        }
    }
}
