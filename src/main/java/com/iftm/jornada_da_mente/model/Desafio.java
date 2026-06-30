package com.iftm.jornada_da_mente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "desafios")
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desafio_id")
    private Long id;

    @Column(name = "desafio_titulo", nullable = false)
    private String titulo;

    @Column(name = "desafio_descricao")
    private String descricao;

    @Column(name = "desafio_duracao_dias", nullable = false)
    private Integer duracaoDias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id")
    private Usuario criador;
}
