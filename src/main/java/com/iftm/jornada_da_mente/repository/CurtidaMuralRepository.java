package com.iftm.jornada_da_mente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iftm.jornada_da_mente.model.CurtidaMural;
import com.iftm.jornada_da_mente.model.MensagemMural;
import com.iftm.jornada_da_mente.model.Usuario;

public interface CurtidaMuralRepository extends JpaRepository<CurtidaMural, Long> {
    Optional<CurtidaMural> findByMensagemAndUsuario(MensagemMural mensagem, Usuario usuario);
    long countByMensagem(MensagemMural mensagem);
    List<CurtidaMural> findByMensagem(MensagemMural mensagem);
}
