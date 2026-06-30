package com.iftm.jornada_da_mente.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "metas")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Long id;

    @Column(name = "meta_titulo", nullable = false)
    private String titulo;

    @Column(name = "meta_descricao")
    private String descricao;

    @Column(name = "meta_pontos", nullable = false)
    private Integer pontos;

    @Column(name = "meta_data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "meta_concluida", nullable = false)
    private boolean concluida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
