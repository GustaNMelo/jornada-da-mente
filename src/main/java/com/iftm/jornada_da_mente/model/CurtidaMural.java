package com.iftm.jornada_da_mente.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "curtidas_mural", uniqueConstraints = @UniqueConstraint(columnNames = {"mensagem_id", "usuario_id"}))
public class CurtidaMural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curtida_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensagem_id", nullable = false)
    private MensagemMural mensagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
