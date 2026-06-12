package com.sicred.votacao.service;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.exception.PautaNotFoundException;
import com.sicred.votacao.exception.SessaoJaExisteException;
import com.sicred.votacao.exception.SessaoEncerradaException;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    @Transactional
    public VotingSessionResponse abrirSessao(Long pautaId, OpenVotingSessionRequest request) {
        // verificar pauta existe
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNotFoundException("Pauta não encontrada"));

        // verificar se já existe sessão
        if (sessaoVotacaoRepository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException("Sessão já existe para esta pauta");
        }

        int duracao = 1; // default 1 minuto
        if (request != null && request.getDuracaoMinutos() != null) {
            if (request.getDuracaoMinutos() <= 0) {
                throw new IllegalArgumentException("duracaoMinutos must be greater than zero");
            }
            duracao = request.getDuracaoMinutos();
        }

        LocalDateTime abertura = LocalDateTime.now();
        LocalDateTime fechamento = abertura.plusMinutes(duracao);

        SessaoVotacao sessao = SessaoVotacao.builder()
                .pauta(pauta)
                .dataAbertura(abertura)
                .dataFechamento(fechamento)
                .build();

        SessaoVotacao saved = sessaoVotacaoRepository.save(sessao);

        log.info("Sessão criada para pauta {} com id {}", pautaId, saved.getId());

        return VotingSessionResponse.builder()
                .id(saved.getId())
                .pautaId(saved.getPauta().getId())
                .dataAbertura(saved.getDataAbertura())
                .dataFechamento(saved.getDataFechamento())
                .build();
    }
}
