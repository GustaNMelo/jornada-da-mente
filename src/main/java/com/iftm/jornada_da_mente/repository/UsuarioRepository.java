package com.iftm.jornada_da_mente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
