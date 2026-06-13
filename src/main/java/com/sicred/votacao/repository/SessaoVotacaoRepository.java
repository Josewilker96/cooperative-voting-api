package com.sicred.votacao.repository;

import com.sicred.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    Optional<SessaoVotacao> findByPautaId(Long pautaId);

    @Query("""
        SELECT s
        FROM SessaoVotacao s
        WHERE s.pauta.id = :pautaId
          AND s.dataFechamento > :agora
    """)
    Optional<SessaoVotacao> findSessaoAtiva(
            @Param("pautaId") Long pautaId,
            @Param("agora") LocalDateTime agora);

    boolean existsByPautaId(Long pautaId);
}