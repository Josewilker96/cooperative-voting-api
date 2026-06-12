package com.sicred.votacao.service;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.VoteResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Transactional
    public VoteResponse registrarVoto(VoteRequest request) {
        // 1. validar pauta existe
        Pauta pauta = pautaRepository.findById(request.getPautaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        // 2. verificar existe sessão para a pauta
        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta"));

        // 3. verificar sessão não encerrada
        if (sessao.getDataFechamento() != null && sessao.getDataFechamento().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sessão encerrada");
        }

        // 4. verificar se associado já votou
        if (votoRepository.existsByPautaIdAndIdentificadorAssociado(pauta.getId(), request.getIdentificadorAssociado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Associado já votou nesta pauta");
        }

        // 5. persistir voto
        Voto voto = Voto.builder()
                .pauta(pauta)
                .identificadorAssociado(request.getIdentificadorAssociado())
                .voto(request.getVoto())
                .dataVoto(LocalDateTime.now())
                .build();

        try {
            Voto saved = votoRepository.save(voto);
            return VoteResponse.builder()
                    .id(saved.getId())
                    .pautaId(saved.getPauta().getId())
                    .identificadorAssociado(saved.getIdentificadorAssociado())
                    .voto(saved.getVoto())
                    .dataVoto(saved.getDataVoto())
                    .build();
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            // Pode acontecer se a constraint única do DB for violada por concorrência
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Associado já votou nesta pauta");
        }
    }
}
