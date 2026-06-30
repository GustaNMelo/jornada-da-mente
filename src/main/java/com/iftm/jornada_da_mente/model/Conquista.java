package com.iftm.jornada_da_mente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "conquistas")
public class Conquista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conquista_id")
    private Long id;

    @Column(name = "conquista_nome", nullable = false)
    private String nome;

    @Column(name = "conquista_icone", nullable = false)
    private String icone;

    @Column(name = "conquista_descricao")
    private String descricao;
}
