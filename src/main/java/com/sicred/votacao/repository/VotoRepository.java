package com.sicred.votacao.repository;

import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.entity.TipoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByPautaIdAndIdentificadorAssociado(Long pautaId, String identificadorAssociado);
    Optional<Voto> findByPautaIdAndIdentificadorAssociado(Long pautaId, String identificadorAssociado);

    // Counts for efficient result calculation
    long countByPautaId(Long pautaId);
    long countByPautaIdAndVoto(Long pautaId, TipoVoto voto);
}
