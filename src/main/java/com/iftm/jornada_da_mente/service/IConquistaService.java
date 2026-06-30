package com.iftm.jornada_da_mente.service;

import java.util.List;

import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.model.UsuarioConquista;

public interface IConquistaService {
    void criarConquistasPadraoSeNecessario();
    void concederConquistaIniciante(Usuario usuario);
    long countByUsuario(Usuario usuario);
    List<UsuarioConquista> getConquistasDoUsuario(Usuario usuario);
}
