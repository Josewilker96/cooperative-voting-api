package com.sicred.votacao.integration;

import com.sicred.votacao.dto.VotingResultResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.TipoVoto;
import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResultadoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveRetornarResultadoDaVotacao() throws Exception {
        // Arrange
        Pauta pauta = pautaRepository.save(Pauta.builder().titulo("Pauta Resultado").dataCriacao(LocalDateTime.now()).build());
        SessaoVotacao sessao = sessaoRepository.save(SessaoVotacao.builder().pauta(pauta).dataAbertura(LocalDateTime.now().minusMinutes(10)).dataFechamento(LocalDateTime.now().plusMinutes(10)).build());

        votoRepository.save(Voto.builder().pauta(pauta).identificadorAssociado("a").voto(TipoVoto.SIM).dataVoto(LocalDateTime.now()).build());
        votoRepository.save(Voto.builder().pauta(pauta).identificadorAssociado("b").voto(TipoVoto.SIM).dataVoto(LocalDateTime.now()).build());
        votoRepository.save(Voto.builder().pauta(pauta).identificadorAssociado("c").voto(TipoVoto.NAO).dataVoto(LocalDateTime.now()).build());

        // Act
        MvcResult result = mockMvc.perform(get(String.format("/api/v1/pautas/%d/resultado", pauta.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String resp = result.getResponse().getContentAsString();
        VotingResultResponse r = objectMapper.readValue(resp, VotingResultResponse.class);

        assertThat(r.getTotalVotos()).isEqualTo(3);
        assertThat(r.getTotalSim()).isEqualTo(2);
        assertThat(r.getTotalNao()).isEqualTo(1);
        assertThat(r.getResultado()).isNotNull();
    }
}
