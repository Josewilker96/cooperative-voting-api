package com.sicred.votacao.service;

import com.sicred.votacao.dto.CreatePautaRequest;
import com.sicred.votacao.dto.PautaResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("deveCriarPautaComSucesso")
    void deveCriarPautaComSucesso() {
        // Arrange
        CreatePautaRequest request = CreatePautaRequest.builder()
                .titulo("Pauta de teste")
                .build();

        Pauta saved = Pauta.builder()
                .id(1L)
                .titulo("Pauta de teste")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(saved);

        // Act
        PautaResponse response = pautaService.createPauta(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Pauta de teste");
        assertThat(response.getDataCriacao()).isNotNull();

        ArgumentCaptor<Pauta> captor = ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository, times(1)).save(captor.capture());
        Pauta captured = captor.getValue();
        assertThat(captured.getTitulo()).isEqualTo("Pauta de teste");
    }

    @Test
    @DisplayName("deveRejeitarTituloVazio")
    void deveRejeitarTituloVazio() {
        // Arrange
        CreatePautaRequest request = CreatePautaRequest.builder().titulo("").build();

        // Act
        Set<ConstraintViolation<CreatePautaRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("titulo is required");

        verifyNoInteractions(pautaRepository);
    }

    @Test
    @DisplayName("deveRejeitarTituloNulo")
    void deveRejeitarTituloNulo() {
        // Arrange
        CreatePautaRequest request = CreatePautaRequest.builder().titulo(null).build();

        // Act
        Set<ConstraintViolation<CreatePautaRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();

        verifyNoInteractions(pautaRepository);
    }
}
