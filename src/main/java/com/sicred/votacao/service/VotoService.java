package com.sicred.votacao.service;

import com.sicred.votacao.dto.SessaoVotacaoCacheDTO;
import com.sicred.votacao.dto.VoteRequest;
import com.sicred.votacao.dto.formulario.BotaoFormulario;
import com.sicred.votacao.dto.formulario.ItemFormulario;
import com.sicred.votacao.dto.formulario.TelaFormulario;
import com.sicred.votacao.entity.Pauta;
import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.exception.CpfInvalidoException;
import com.sicred.votacao.exception.VotoDuplicadoException;
import com.sicred.votacao.integration.VoterEligibilityClient;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import com.sicred.votacao.repository.PautaRepository;
import com.sicred.votacao.repository.VotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VoterEligibilityClient voterEligibilityClient;

    public TelaFormulario processarVoto(Long pautaId, VoteRequest request) {

        log.info(
                "Processando voto: pautaId={}, associado={}",
                pautaId,
                request.getIdentificadorAssociado()
        );

        try {
            UserInfoResponse userInfo =
                    voterEligibilityClient.checkCpf(
                            request.getIdentificadorAssociado()
                    );

            if ("UNABLE_TO_VOTE".equals(userInfo.getStatus())) {
                return telaErro("Associado não habilitado para votar");
            }

        } catch (CpfInvalidoException e) {
            return telaErro("CPF inválido");
        }

        Pauta pauta = pautaRepository.findById(pautaId).orElse(null);

        if (pauta == null) {
            return telaErro("Pauta não encontrada");
        }

        SessaoVotacaoCacheDTO sessao =
                sessaoVotacaoService.buscarSessaoAtivaPorPauta(pautaId);

        log.info("Sessão retornada pelo cache/service: {}", sessao);

        if (sessao == null) {
            return telaErro("Sessão não encontrada ou encerrada");
        }

        if (sessao.getDataFechamento().isBefore(LocalDateTime.now())) {
            return telaErro("Sessão não encontrada ou encerrada");
        }

        String votoOriginal = request.getVoto().name();
        String votoAlternativo = "SIM".equals(votoOriginal) ? "NAO" : "SIM";

        String urlRegistrar =
                "http://localhost:8081/api/v1/pautas/" + pautaId + "/votos/registrar";

        return TelaFormulario.builder()
                .tipo("FORMULARIO")
                .titulo("Confirmar Voto")
                .itens(List.of(
                        ItemFormulario.builder()
                                .tipo("TEXTO")
                                .label("Pauta")
                                .valor(pauta.getTitulo())
                                .build(),

                        ItemFormulario.builder()
                                .tipo("TEXTO")
                                .label("Seu voto")
                                .valor(votoOriginal)
                                .build()
                ))
                .botaoOk(
                        BotaoFormulario.builder()
                                .texto("Confirmar")
                                .url(urlRegistrar)
                                .body(Map.of(
                                        "identificadorAssociado",
                                        request.getIdentificadorAssociado(),
                                        "voto",
                                        votoOriginal
                                ))
                                .build()
                )
                .botaoCancelar(
                        BotaoFormulario.builder()
                                .texto("Votar " + votoAlternativo)
                                .url(urlRegistrar)
                                .body(Map.of(
                                        "identificadorAssociado",
                                        request.getIdentificadorAssociado(),
                                        "voto",
                                        votoAlternativo
                                ))
                                .build()
                )
                .build();
    }

    @Transactional
    public void registrarVoto(Long pautaId, VoteRequest request) {

        SessaoVotacaoCacheDTO sessao =
                sessaoVotacaoService.buscarSessaoAtivaPorPauta(pautaId);

        if (sessao == null) {
            throw new RuntimeException("Sessão não encontrada");
        }

        if (sessao.getDataFechamento().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Sessão encerrada");
        }

        Pauta pauta = pautaRepository.getReferenceById(pautaId);

        Voto voto = Voto.builder()
                .pauta(pauta)
                .identificadorAssociado(request.getIdentificadorAssociado())
                .voto(request.getVoto())
                .dataVoto(LocalDateTime.now())
                .build();

        try {

            votoRepository.save(voto);

            log.info(
                    "Voto registrado: pautaId={}, associado={}",
                    pautaId,
                    request.getIdentificadorAssociado()
            );

        } catch (DataIntegrityViolationException ex) {

            log.warn(
                    "Voto duplicado bloqueado pelo banco: pautaId={}, associado={}",
                    pautaId,
                    request.getIdentificadorAssociado()
            );

            throw new VotoDuplicadoException(
                    "Associado já votou nesta pauta"
            );
        }
    }

    private TelaFormulario telaErro(String mensagem) {

        return TelaFormulario.builder()
                .tipo("FORMULARIO")
                .titulo("Erro")
                .itens(List.of(
                        ItemFormulario.builder()
                                .tipo("TEXTO")
                                .label("Mensagem")
                                .valor(mensagem)
                                .build()
                ))
                .botaoOk(
                        BotaoFormulario.builder()
                                .texto("OK")
                                .url("http://localhost:8081/api/v1/pautas")
                                .body(Map.of())
                                .build()
                )
                .build();
    }
}