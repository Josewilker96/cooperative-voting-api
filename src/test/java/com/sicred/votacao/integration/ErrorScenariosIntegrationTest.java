package com.sicred.votacao.integration;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import com.sicred.votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorScenariosIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    void deveRetornar404QuandoPautaNaoExistir() throws Exception {
        // Arrange
        long nonExistId = 9999L;

        // Act / Assert
        mockMvc.perform(get(String.format("/api/v1/pautas/%d/resultado", nonExistId)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoPayloadInvalidoAoRegistrarVoto() throws Exception {
        // Arrange
        VoteRequest invalid = VoteRequest.builder().pautaId(null).identificadorAssociado("").voto(null).build();
        String json = objectMapper.writeValueAsString(invalid);

        // Act / Assert
        mockMvc.perform(post("/api/v1/votos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoSessaoNaoExistirAoRegistrarVoto() throws Exception {
        // Arrange
        Pauta pauta = pautaRepository.save(Pauta.builder().titulo("Pauta erro").dataCriacao(LocalDateTime.now()).build());
        String cpf = "123";
        // ensure external client returns a value to avoid NPE in service
        when(voterEligibilityClient.checkCpf(anyString())).thenReturn(UserInfoResponse.builder().status("ABLE_TO_VOTE").build());

        // quick sanity check that mock returns non-null
        UserInfoResponse sanity = voterEligibilityClient.checkCpf(cpf);
        assertThat(sanity).isNotNull();
        assertThat(sanity.getStatus()).isEqualTo("ABLE_TO_VOTE");

        VoteRequest req = VoteRequest.builder().pautaId(pauta.getId()).identificadorAssociado(cpf).voto(com.sicred.votacao.entity.TipoVoto.SIM).build();
        String json = objectMapper.writeValueAsString(req);

        // Act / Assert
        mockMvc.perform(post("/api/v1/votos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }
}
