package com.sicred.votacao.service;

import com.sicred.votacao.dto.CreatePautaRequest;
import com.sicred.votacao.dto.PautaResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.repository.PautaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    @Transactional
    public PautaResponse createPauta(CreatePautaRequest request) {
        Pauta pauta = Pauta.builder()
                .titulo(request.getTitulo())
                .dataCriacao(LocalDateTime.now())
                .build();

        Pauta saved = pautaRepository.save(pauta);

        return PautaResponse.builder()
                .id(saved.getId())
                .titulo(saved.getTitulo())
                .dataCriacao(saved.getDataCriacao())
                .build();
    }
}
