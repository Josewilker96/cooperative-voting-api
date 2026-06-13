//package com.sicred.votacao.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "voto", uniqueConstraints = {
//        @UniqueConstraint(name = "uk_voto_pauta_associado", columnNames = {"pauta_id", "identificador_associado"})
//})
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Voto {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "pauta_id", nullable = false)
//    private Pauta pauta;
//
//    @Column(name = "identificador_associado", nullable = false)
//    private String identificadorAssociado;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TipoVoto voto;
//
//    @Column(name = "data_voto", nullable = false)
//    private LocalDateTime dataVoto;
//
//}

package com.sicred.votacao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_voto_pauta_associado", columnNames = {"pauta_id", "identificador_associado"})
        },
        indexes = {
                @Index(name = "idx_voto_pauta", columnList = "pauta_id"),
                @Index(name = "idx_voto_associado", columnList = "identificador_associado")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "identificador_associado", nullable = false)
    private String identificadorAssociado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVoto voto;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto;
}