package com.sicred.votacao.service;

import com.sicred.votacao.dto.VotingResultResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.TipoVoto;
import com.sicred.votacao.exception.SessaoAindaAbertaException;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotingResultService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    @Cacheable(value = "resultados", key = "#pautaId")
    public VotingResultResponse apurarResultado(Long pautaId) {

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pauta não encontrada"));

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pautaId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sessão não encontrada para a pauta"));

        if (sessao.getDataFechamento().isAfter(LocalDateTime.now())) {
            throw new SessaoAindaAbertaException("Sessão ainda está aberta");
        }

        long totalSim = votoRepository.countByPautaIdAndVoto(
                pautaId,
                TipoVoto.SIM
        );

        long totalNao = votoRepository.countByPautaIdAndVoto(
                pautaId,
                TipoVoto.NAO
        );

        long total = totalSim + totalNao;

        String resultado;

        if (totalSim > totalNao) {
            resultado = "APROVADA";
        } else if (totalNao > totalSim) {
            resultado = "REJEITADA";
        } else {
            resultado = "EMPATADA";
        }

        return VotingResultResponse.builder()
                .pautaId(pauta.getId())
                .titulo(pauta.getTitulo())
                .totalVotos(total)
                .totalSim(totalSim)
                .totalNao(totalNao)
                .resultado(resultado)
                .build();
    }
}