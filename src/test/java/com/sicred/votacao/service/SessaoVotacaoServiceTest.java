package com.sicred.votacao.service;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.exception.PautaNotFoundException;
import com.sicred.votacao.exception.SessaoJaExisteException;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    private SessaoVotacaoService service;

    @Captor
    private ArgumentCaptor<SessaoVotacao> sessaoCaptor;

    @BeforeEach
    void setUp() {
        service = new SessaoVotacaoService(sessaoVotacaoRepository, pautaRepository);
    }

    @Test
    void deveAbrirSessaoComDuracaoInformada() {
        // Arrange
        Long pautaId = 1L;
        Pauta pauta = Pauta.builder().id(pautaId).titulo("Pauta 1").dataCriacao(LocalDateTime.now()).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        OpenVotingSessionRequest request = OpenVotingSessionRequest.builder().duracaoMinutos(5).build();

        // Act
        VotingSessionResponse resp = service.abrirSessao(pautaId, request);

        // Assert
        verify(sessaoVotacaoRepository).save(sessaoCaptor.capture());
        SessaoVotacao saved = sessaoCaptor.getValue();

        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getPautaId()).isEqualTo(pautaId);
        assertThat(saved.getDataAbertura()).isNotNull();
        assertThat(saved.getDataFechamento()).isNotNull();

        long minutes = Duration.between(saved.getDataAbertura(), saved.getDataFechamento()).toMinutes();
        assertThat(minutes).isEqualTo(5);

        verify(sessaoVotacaoRepository, times(1)).existsByPautaId(pautaId);
    }

    @Test
    void deveAbrirSessaoComDuracaoPadrao() {
        // Arrange
        Long pautaId = 2L;
        Pauta pauta = Pauta.builder().id(pautaId).titulo("Pauta 2").dataCriacao(LocalDateTime.now()).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao s = invocation.getArgument(0);
            s.setId(11L);
            return s;
        });

        // Act
        VotingSessionResponse resp = service.abrirSessao(pautaId, null);

        // Assert
        verify(sessaoVotacaoRepository).save(sessaoCaptor.capture());
        SessaoVotacao saved = sessaoCaptor.getValue();

        assertThat(resp.getId()).isEqualTo(11L);
        assertThat(resp.getPautaId()).isEqualTo(pautaId);
        assertThat(saved.getDataAbertura()).isNotNull();
        assertThat(saved.getDataFechamento()).isNotNull();

        long minutes = Duration.between(saved.getDataAbertura(), saved.getDataFechamento()).toMinutes();
        assertThat(minutes).isEqualTo(1);
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoExistir() {
        // Arrange
        Long pautaId = 3L;
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> service.abrirSessao(pautaId, null))
                .isInstanceOf(PautaNotFoundException.class);

        verify(sessaoVotacaoRepository, never()).save(any());
    }

    @Test
    void deveImpedirSessaoDuplicada() {
        // Arrange
        Long pautaId = 4L;
        Pauta pauta = Pauta.builder().id(pautaId).titulo("Pauta 4").dataCriacao(LocalDateTime.now()).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(pautaId)).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> service.abrirSessao(pautaId, null))
                .isInstanceOf(SessaoJaExisteException.class);

        verify(sessaoVotacaoRepository, never()).save(any());
    }

    @Test
    void deveRejeitarDuracaoInvalida() {
        // Arrange
        Long pautaId = 5L;
        Pauta pauta = Pauta.builder().id(pautaId).titulo("Pauta 5").dataCriacao(LocalDateTime.now()).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(pautaId)).thenReturn(false);

        OpenVotingSessionRequest request = OpenVotingSessionRequest.builder().duracaoMinutos(0).build();

        // Act / Assert
        assertThatThrownBy(() -> service.abrirSessao(pautaId, request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(sessaoVotacaoRepository, never()).save(any());
    }
}
