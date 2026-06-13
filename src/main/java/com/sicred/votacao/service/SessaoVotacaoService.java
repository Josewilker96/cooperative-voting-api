package com.sicred.votacao.service;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.SessaoVotacaoCacheDTO;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.exception.PautaNotFoundException;
import com.sicred.votacao.exception.SessaoJaExisteException;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    @Transactional
    @CacheEvict(value = "sessoes", key = "#pautaId")
    public VotingSessionResponse abrirSessao(Long pautaId, OpenVotingSessionRequest request) {

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNotFoundException("Pauta não encontrada"));

        if (sessaoVotacaoRepository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException("Sessão já existe para esta pauta");
        }

        int duracao = 1;

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

        log.info(
                "Sessão criada para pauta {} com id {}",
                pautaId,
                saved.getId()
        );

        return VotingSessionResponse.builder()
                .id(saved.getId())
                .pautaId(saved.getPauta().getId())
                .dataAbertura(saved.getDataAbertura())
                .dataFechamento(saved.getDataFechamento())
                .build();
    }

    @Cacheable(value = "sessoes", key = "#pautaId", unless = "#result == null")
    public SessaoVotacaoCacheDTO buscarSessaoAtivaPorPauta(Long pautaId) {

        log.info("BUSCANDO SESSAO ATIVA");
        log.info("PautaId: {}", pautaId);

        var sessaoOpt = sessaoVotacaoRepository.findSessaoAtiva(
                pautaId,
                LocalDateTime.now()
        );

        if (sessaoOpt.isEmpty()) {

            log.warn("NENHUMA SESSAO ATIVA ENCONTRADA");

            var sessaoPauta = sessaoVotacaoRepository.findByPautaId(pautaId);

            if (sessaoPauta.isPresent()) {
                log.info(
                        "Sessao existe no banco. Fechamento={}",
                        sessaoPauta.get().getDataFechamento()
                );
            }

            return null;
        }

        SessaoVotacao sessao = sessaoOpt.get();

        log.info(
                "SESSAO ENCONTRADA -> id={}, abertura={}, fechamento={}",
                sessao.getId(),
                sessao.getDataAbertura(),
                sessao.getDataFechamento()
        );

        return SessaoVotacaoCacheDTO.builder()
                .id(sessao.getId())
                .pautaId(sessao.getPauta().getId())
                .dataAbertura(sessao.getDataAbertura())
                .dataFechamento(sessao.getDataFechamento())
                .build();
    }
}