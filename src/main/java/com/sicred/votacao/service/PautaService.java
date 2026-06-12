package com.sicred.votacao.service;

import com.sicred.votacao.dto.CreatePautaRequest;
import com.sicred.votacao.dto.PautaResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.repository.PautaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PautaService {

    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;

    @Transactional
    public PautaResponse createPauta(CreatePautaRequest request) {
        Pauta pauta = Pauta.builder()
                .titulo(request.getTitulo())
                .dataCriacao(LocalDateTime.now())
                .build();

        Pauta saved = pautaRepository.save(pauta);

        log.info("Pauta criada: id={}, titulo={}", saved.getId(), saved.getTitulo());

        return PautaResponse.builder()
                .id(saved.getId())
                .titulo(saved.getTitulo())
                .dataCriacao(saved.getDataCriacao())
                .build();
    }
}
