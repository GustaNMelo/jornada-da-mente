package com.iftm.jornada_da_mente.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "participacoes_desafio")
public class ParticipacaoDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participacao_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desafio_id", nullable = false)
    private Desafio desafio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "dias_completados", nullable = false)
    private Integer diasCompletados = 0;

    @Column(name = "pontos", nullable = false)
    private Integer pontos = 0;

    @Column(name = "ultimo_registro")
    private LocalDate ultimoRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusParticipacao status = StatusParticipacao.ACEITA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convidado_por_id")
    private Usuario convidadoPor;
}
