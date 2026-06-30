package com.iftm.jornada_da_mente.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.Meta;
import com.iftm.jornada_da_mente.model.Usuario;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByUsuarioOrderByDataReferenciaAsc(Usuario usuario);
    long countByUsuarioAndConcluidaFalse(Usuario usuario);
    long countByUsuarioAndConcluidaTrue(Usuario usuario);
    List<Meta> findByUsuarioAndConcluidaFalseOrderByDataReferenciaAsc(Usuario usuario);
    List<Meta> findByUsuarioAndDataReferencia(Usuario usuario, LocalDate dataReferencia);
}
