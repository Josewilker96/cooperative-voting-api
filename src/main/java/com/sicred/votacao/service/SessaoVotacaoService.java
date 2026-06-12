package com.sicred.votacao.service;

import com.sicred.votacao.dto.OpenVotingSessionRequest;
import com.sicred.votacao.dto.VotingSessionResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    @Transactional
    public VotingSessionResponse abrirSessao(Long pautaId, OpenVotingSessionRequest request) {
        // verificar pauta existe
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        // verificar se já existe sessão
        if (sessaoVotacaoRepository.existsByPautaId(pautaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sessão já existe para esta pauta");
        }

        int duracao = 1; // default 1 minuto
        if (request != null && request.getDuracaoMinutos() != null) {
            if (request.getDuracaoMinutos() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "duracaoMinutos must be greater than zero");
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

        return VotingSessionResponse.builder()
                .id(saved.getId())
                .pautaId(saved.getPauta().getId())
                .dataAbertura(saved.getDataAbertura())
                .dataFechamento(saved.getDataFechamento())
                .build();
    }
}
