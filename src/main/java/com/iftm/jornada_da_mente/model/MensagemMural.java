package com.iftm.jornada_da_mente.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mensagens_mural")
public class MensagemMural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mensagem_id")
    private Long id;

    @Column(name = "mensagem_texto", nullable = false, length = 500)
    private String texto;

    @Column(name = "mensagem_data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;
}
