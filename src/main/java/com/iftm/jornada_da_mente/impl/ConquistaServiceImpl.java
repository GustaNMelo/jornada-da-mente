package com.iftm.jornada_da_mente.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iftm.jornada_da_mente.model.Conquista;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.model.UsuarioConquista;
import com.iftm.jornada_da_mente.repository.ConquistaRepository;
import com.iftm.jornada_da_mente.repository.UsuarioConquistaRepository;
import com.iftm.jornada_da_mente.service.IConquistaService;

@Service
public class ConquistaServiceImpl implements IConquistaService {

    private static final String NOME_INICIANTE = "Iniciante";

    @Autowired
    private ConquistaRepository conquistaRepository;

    @Autowired
    private UsuarioConquistaRepository usuarioConquistaRepository;

    @Override
    public void criarConquistasPadraoSeNecessario() {
        if (conquistaRepository.count() == 0) {
            Conquista iniciante = new Conquista();
            iniciante.setNome(NOME_INICIANTE);
            iniciante.setIcone("⭐");
            iniciante.setDescricao("Deu o primeiro passo na sua jornada");
            conquistaRepository.save(iniciante);
        }
    }

    @Override
    public void concederConquistaIniciante(Usuario usuario) {
        Conquista iniciante = conquistaRepository.findAll().stream()
                .filter(c -> NOME_INICIANTE.equals(c.getNome()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Conquista padrão não encontrada."));

        UsuarioConquista uc = new UsuarioConquista();
        uc.setUsuario(usuario);
        uc.setConquista(iniciante);
        usuarioConquistaRepository.save(uc);
    }

    @Override
    public long countByUsuario(Usuario usuario) {
        return usuarioConquistaRepository.countByUsuario(usuario);
    }

    @Override
    public List<UsuarioConquista> getConquistasDoUsuario(Usuario usuario) {
        return usuarioConquistaRepository.findByUsuario(usuario);
    }
}
