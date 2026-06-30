package com.iftm.jornada_da_mente.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(name = "usuario_nome", nullable = false)
    private String nome;

    @Column(name = "usuario_email", nullable = false, unique = true)
    private String email;

    @Column(name = "usuario_senha", nullable = false)
    private String senha;

    @Column(name = "usuario_nivel", nullable = false)
    private Integer nivel = 1;

    @Column(name = "usuario_titulo_nivel", nullable = false)
    private String tituloNivel = "Iniciante";

    @Column(name = "usuario_pontos_totais", nullable = false)
    private Integer pontosTotais = 0;

    @Column(name = "usuario_dias_seguidos", nullable = false)
    private Integer diasSeguidos = 0;

    @Column(name = "usuario_data_cadastro", nullable = false)
    private LocalDate dataCadastro = LocalDate.now();
}
