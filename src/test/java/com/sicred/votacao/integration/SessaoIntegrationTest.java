package com.sicred.votacao.integration;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoRepository;

    @Test
    void deveAbrirSessaoComSucesso() throws Exception {
        // Arrange
        Pauta pauta = pautaRepository.save(Pauta.builder().titulo("Pauta Sessao").dataCriacao(LocalDateTime.now()).build());
        OpenVotingSessionRequest req = OpenVotingSessionRequest.builder().duracaoMinutos(2).build();
        String json = objectMapper.writeValueAsString(req);

        // Act
        MvcResult result = mockMvc.perform(post(String.format("/api/v1/pautas/%d/sessao", pauta.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String resp = result.getResponse().getContentAsString();
        VotingSessionResponse s = objectMapper.readValue(resp, VotingSessionResponse.class);
        assertThat(s.getId()).isNotNull();
        assertThat(s.getDataAbertura()).isNotNull();
        assertThat(s.getDataFechamento()).isNotNull();

        long minutes = Duration.between(s.getDataAbertura(), s.getDataFechamento()).toMinutes();
        assertThat(minutes).isEqualTo(2);

        var persisted = sessaoRepository.findById(s.getId()).orElse(null);
        assertThat(persisted).isNotNull();
    }
}
