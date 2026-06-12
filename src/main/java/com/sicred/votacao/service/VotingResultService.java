package com.sicred.votacao.service;

import com.sicred.votacao.dto.VotingResultResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.TipoVoto;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VotingResultService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    public VotingResultResponse apurarResultado(Long pautaId) {
        // 1. pauta existe
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        // 2. sessão existe
        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta"));

        // 3. contabilizar votos utilizando contagens eficientes
        long totalSim = votoRepository.countByPautaIdAndVoto(pautaId, TipoVoto.SIM);
        long totalNao = votoRepository.countByPautaIdAndVoto(pautaId, TipoVoto.NAO);
        long total = votoRepository.countByPautaId(pautaId);

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
