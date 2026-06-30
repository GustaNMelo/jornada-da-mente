package com.iftm.jornada_da_mente.service;

import java.util.List;
import java.util.Optional;

import com.iftm.jornada_da_mente.model.Desafio;
import com.iftm.jornada_da_mente.model.ParticipacaoDesafio;
import com.iftm.jornada_da_mente.model.Usuario;

public interface IDesafioService {
    void criarDesafioPadraoSeNecessario();
    List<Desafio> getAllDesafios();
    Optional<Desafio> getDesafioDestaque();
    Desafio getDesafioById(Long id);
    Desafio criarDesafioComCriador(Desafio desafio, Usuario criador);
    List<ParticipacaoDesafio> getRanking(Desafio desafio);
    ParticipacaoDesafio getOrCriarParticipacao(Desafio desafio, Usuario usuario);
    void registrarProgressoHoje(Long desafioId, Usuario usuario);
    void simularNovoDia(Long desafioId, Usuario usuario);

    List<Desafio> getDesafiosDoUsuario(Usuario usuario);
    boolean usuarioParticipaAceito(Desafio desafio, Usuario usuario);
    boolean usuarioECriador(Desafio desafio, Usuario usuario);
    void convidarUsuario(Long desafioId, Long usuarioConvidadoId, Usuario quemConvida);
    List<ParticipacaoDesafio> getConvitesPendentes(Usuario usuario);
    void aceitarConvite(Long participacaoId, Usuario usuario);
    void recusarConvite(Long participacaoId, Usuario usuario);
    List<Usuario> getUsuariosConvidaveis(Desafio desafio, Usuario quemConvida);
}
