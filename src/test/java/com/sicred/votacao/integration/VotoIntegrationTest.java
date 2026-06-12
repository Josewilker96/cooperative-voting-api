package com.sicred.votacao.integration;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.VoteResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.TipoVoto;
import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VotoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveRegistrarVotoComSucesso() throws Exception {
        // Arrange
        Pauta pauta = pautaRepository.save(Pauta.builder().titulo("Pauta Voto").dataCriacao(LocalDateTime.now()).build());
        SessaoVotacao sessao = sessaoRepository.save(SessaoVotacao.builder().pauta(pauta).dataAbertura(LocalDateTime.now().minusMinutes(1)).dataFechamento(LocalDateTime.now().plusMinutes(5)).build());

        String cpf = "12345678900";
        when(voterEligibilityClient.checkCpf(cpf)).thenReturn(UserInfoResponse.builder().status("ABLE_TO_VOTE").build());

        VoteRequest req = VoteRequest.builder().pautaId(pauta.getId()).identificadorAssociado(cpf).voto(TipoVoto.SIM).build();
        String json = objectMapper.writeValueAsString(req);

        // Act
        MvcResult result = mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String resp = result.getResponse().getContentAsString();
        VoteResponse voto = objectMapper.readValue(resp, VoteResponse.class);
        assertThat(voto.getId()).isNotNull();
        assertThat(votoRepository.existsByPautaIdAndIdentificadorAssociado(pauta.getId(), cpf)).isTrue();
    }

    @Test
    void deveImpedirVotoDuplicado() throws Exception {
        // Arrange
        Pauta pauta = pautaRepository.save(Pauta.builder().titulo("Pauta Voto 2").dataCriacao(LocalDateTime.now()).build());
        SessaoVotacao sessao = sessaoRepository.save(SessaoVotacao.builder().pauta(pauta).dataAbertura(LocalDateTime.now().minusMinutes(1)).dataFechamento(LocalDateTime.now().plusMinutes(5)).build());

        String cpf = "22222222222";
        when(voterEligibilityClient.checkCpf(cpf)).thenReturn(UserInfoResponse.builder().status("ABLE_TO_VOTE").build());

        Voto existing = Voto.builder().pauta(pauta).identificadorAssociado(cpf).voto(TipoVoto.SIM).dataVoto(LocalDateTime.now()).build();
        votoRepository.save(existing);

        VoteRequest req = VoteRequest.builder().pautaId(pauta.getId()).identificadorAssociado(cpf).voto(TipoVoto.SIM).build();
        String json = objectMapper.writeValueAsString(req);

        // Act / Assert
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
