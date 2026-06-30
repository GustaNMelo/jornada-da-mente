package com.iftm.jornada_da_mente.service;

import java.util.List;
import java.util.Optional;

import com.iftm.jornada_da_mente.model.Meta;
import com.iftm.jornada_da_mente.model.Usuario;

public interface IMetaService {
    void criarMetasPadrao(Usuario usuario);
    List<Meta> getMetasDoUsuario(Usuario usuario);
    Optional<Meta> getProximaMetaPendente(Usuario usuario);
    long getMetasAtivasCount(Usuario usuario);
    long getMetasCompletadasCount(Usuario usuario);
    Meta salvarMeta(Meta meta);
    Meta getMetaById(Long id);
    void concluirMeta(Long id, Usuario usuario);
    void desmarcarMeta(Long id, Usuario usuario);
    void excluirMeta(Long id, Usuario usuario);
}
