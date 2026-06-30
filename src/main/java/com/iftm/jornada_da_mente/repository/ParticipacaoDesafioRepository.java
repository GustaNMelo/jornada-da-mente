package com.iftm.jornada_da_mente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.Desafio;
import com.iftm.jornada_da_mente.model.ParticipacaoDesafio;
import com.iftm.jornada_da_mente.model.StatusParticipacao;
import com.iftm.jornada_da_mente.model.Usuario;

public interface ParticipacaoDesafioRepository extends JpaRepository<ParticipacaoDesafio, Long> {
    List<ParticipacaoDesafio> findByDesafioOrderByPontosDesc(Desafio desafio);
    List<ParticipacaoDesafio> findByDesafioAndStatusOrderByPontosDesc(Desafio desafio, StatusParticipacao status);
    Optional<ParticipacaoDesafio> findByDesafioAndUsuario(Desafio desafio, Usuario usuario);
    List<ParticipacaoDesafio> findByUsuario(Usuario usuario);
    List<ParticipacaoDesafio> findByUsuarioAndStatus(Usuario usuario, StatusParticipacao status);
}
