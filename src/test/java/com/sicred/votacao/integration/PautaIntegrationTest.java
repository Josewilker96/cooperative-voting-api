package com.sicred.votacao.integration;

import com.sicred.votacao.dto.CreatePautaRequest;
import com.sicred.votacao.dto.PautaResponse;
import com.sicred.votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PautaIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    void deveCriarPautaComSucesso() throws Exception {
        // Arrange
        CreatePautaRequest req = CreatePautaRequest.builder().titulo("Nova Pauta").build();
        String json = objectMapper.writeValueAsString(req);

        // Act
        MvcResult result = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String resp = result.getResponse().getContentAsString();
        PautaResponse pautaResp = objectMapper.readValue(resp, PautaResponse.class);
        assertThat(pautaResp.getId()).isNotNull();
        assertThat(pautaResp.getTitulo()).isEqualTo("Nova Pauta");

        // Persistence
        var persisted = pautaRepository.findById(pautaResp.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted.getTitulo()).isEqualTo("Nova Pauta");
    }
}
