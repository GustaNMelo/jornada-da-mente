package com.iftm.jornada_da_mente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.model.UsuarioConquista;

public interface UsuarioConquistaRepository extends JpaRepository<UsuarioConquista, Long> {
    List<UsuarioConquista> findByUsuario(Usuario usuario);
    long countByUsuario(Usuario usuario);
}
