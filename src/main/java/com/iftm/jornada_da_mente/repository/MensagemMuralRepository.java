package com.iftm.jornada_da_mente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.MensagemMural;

public interface MensagemMuralRepository extends JpaRepository<MensagemMural, Long> {
    List<MensagemMural> findAllByOrderByDataCriacaoDesc();
}
