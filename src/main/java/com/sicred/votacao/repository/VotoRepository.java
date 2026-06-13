package com.sicred.votacao.repository;

import com.sicred.votacao.entity.Voto;
import com.sicred.votacao.entity.TipoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByPautaId(Long pautaId);
    long countByPautaIdAndVoto(Long pautaId, TipoVoto voto);
}