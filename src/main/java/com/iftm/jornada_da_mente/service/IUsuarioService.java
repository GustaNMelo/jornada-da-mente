package com.iftm.jornada_da_mente.service;

import java.util.List;

import com.iftm.jornada_da_mente.model.Usuario;

public interface IUsuarioService {
    Usuario registrarUsuario(Usuario usuario);
    Usuario getUsuarioByEmail(String email);
    Usuario getUsuarioById(Long id);
    Usuario salvarUsuario(Usuario usuario);
    boolean emailJaCadastrado(String email);
    List<Usuario> getAllUsuarios();
}
