package com.sicred.votacao.service;

import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.VoteResponse;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.SessaoVotacao;
import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.exception.PautaNotFoundException;
import com.sicred.votacao.exception.SessaoEncerradaException;
import com.sicred.votacao.exception.SessaoNotFoundException;
import com.sicred.votacao.exception.VotoDuplicadoException;
import com.sicred.votacao.integration.VoterEligibilityClient;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.SessaoVotacaoRepository;
import com.sicred.votacao.repository.VotoRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VoterEligibilityClient voterEligibilityClient;

    @Transactional
    public VoteResponse registrarVoto(VoteRequest request) {
        // 0. validar CPF externo
        log.info("Iniciando validação de CPF para associado={}", request.getIdentificadorAssociado());
        UserInfoResponse userInfo = voterEligibilityClient.checkCpf(request.getIdentificadorAssociado());
        log.info("Resultado validação CPF associado={}: {}", request.getIdentificadorAssociado(), userInfo.getStatus());

        // 1. validar pauta existe
        Pauta pauta = pautaRepository.findById(request.getPautaId())
                .orElseThrow(() -> new PautaNotFoundException("Pauta não encontrada"));

        // 2. verificar existe sessão para a pauta
        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
                .orElseThrow(() -> new SessaoNotFoundException("Sessão não encontrada para a pauta"));

        // 3. verificar sessão não encerrada
        if (sessao.getDataFechamento() != null && sessao.getDataFechamento().isBefore(LocalDateTime.now())) {
            throw new SessaoEncerradaException("Sessão encerrada");
        }

        // 4. verificar se associado já votou
        if (votoRepository.existsByPautaIdAndIdentificadorAssociado(pauta.getId(), request.getIdentificadorAssociado())) {
            throw new VotoDuplicadoException("Associado já votou nesta pauta");
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
            log.info("Voto registrado: pautaId={}, associado={}", pauta.getId(), request.getIdentificadorAssociado());
            return VoteResponse.builder()
                    .id(saved.getId())
                    .pautaId(saved.getPauta().getId())
                    .identificadorAssociado(saved.getIdentificadorAssociado())
                    .voto(saved.getVoto())
                    .dataVoto(saved.getDataVoto())
                    .build();
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            // Pode acontecer se a constraint única do DB for violada por concorrência
            log.warn("Constraint violation when saving vote: pautaId={}, associado={}", pauta.getId(), request.getIdentificadorAssociado());
            throw new VotoDuplicadoException("Associado já votou nesta pauta");
        }
    }
}
