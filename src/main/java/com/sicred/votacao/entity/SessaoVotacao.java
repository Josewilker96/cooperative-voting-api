//package com.sicred.votacao.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "sessao_votacao")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//
//public class SessaoVotacao {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "pauta_id", nullable = false)
//    private Pauta pauta;
//
//    @Column(name = "data_abertura", nullable = false)
//    private LocalDateTime dataAbertura;
//
//    @Column(name = "data_fechamento")
//    private LocalDateTime dataFechamento;
//
//}

package com.sicred.votacao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao", indexes = {
        @Index(name = "idx_sessao_pauta", columnList = "pauta_id"),
        @Index(name = "idx_sessao_pauta_fechamento", columnList = "pauta_id, data_fechamento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;
}