package com.iftm.jornada_da_mente.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iftm.jornada_da_mente.model.Meta;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.repository.MetaRepository;
import com.iftm.jornada_da_mente.service.IMetaService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Service
public class MetaServiceImpl implements IMetaService {

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public void criarMetasPadrao(Usuario usuario) {
        LocalDate hoje = LocalDate.now();

        Meta m1 = new Meta();
        m1.setTitulo("Beber 2L de água");
        m1.setDescricao("Completada hoje");
        m1.setPontos(30);
        m1.setDataReferencia(hoje);
        m1.setConcluida(true);
        m1.setUsuario(usuario);
        metaRepository.save(m1);

        Meta m2 = new Meta();
        m2.setTitulo("Meditação 10min");
        m2.setDescricao("Faça hoje para manter o hábito");
        m2.setPontos(20);
        m2.setDataReferencia(hoje);
        m2.setConcluida(false);
        m2.setUsuario(usuario);
        metaRepository.save(m2);

        Meta m3 = new Meta();
        m3.setTitulo("Registro do dia");
        m3.setDescricao("Reflita sobre seu progresso");
        m3.setPontos(15);
        m3.setDataReferencia(hoje);
        m3.setConcluida(false);
        m3.setUsuario(usuario);
        metaRepository.save(m3);

        Meta m4 = new Meta();
        m4.setTitulo("Caminhada 30min");
        m4.setDescricao("Mantenha-se ativo");
        m4.setPontos(50);
        m4.setDataReferencia(hoje.plusDays(1));
        m4.setConcluida(false);
        m4.setUsuario(usuario);
        metaRepository.save(m4);
    }

    @Override
    public List<Meta> getMetasDoUsuario(Usuario usuario) {
        return metaRepository.findByUsuarioOrderByDataReferenciaAsc(usuario);
    }

    @Override
    public Optional<Meta> getProximaMetaPendente(Usuario usuario) {
        return metaRepository.findByUsuarioAndConcluidaFalseOrderByDataReferenciaAsc(usuario)
                .stream().findFirst();
    }

    @Override
    public long getMetasAtivasCount(Usuario usuario) {
        return metaRepository.countByUsuarioAndConcluidaFalse(usuario);
    }

    @Override
    public long getMetasCompletadasCount(Usuario usuario) {
        return metaRepository.countByUsuarioAndConcluidaTrue(usuario);
    }

    @Override
    public Meta salvarMeta(Meta meta) {
        return metaRepository.save(meta);
    }

    @Override
    public Meta getMetaById(Long id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada: " + id));
    }

    @Override
    public void concluirMeta(Long id, Usuario usuario) {
        Meta meta = getMetaById(id);
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta meta não pertence ao usuário logado.");
        }
        if (meta.isConcluida()) {
            return;
        }
        meta.setConcluida(true);
        metaRepository.save(meta);

        usuario.setPontosTotais(usuario.getPontosTotais() + meta.getPontos());
        usuarioService.salvarUsuario(usuario);
    }

    @Override
    public void desmarcarMeta(Long id, Usuario usuario) {
        Meta meta = getMetaById(id);
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta meta não pertence ao usuário logado.");
        }
        if (!meta.isConcluida()) {
            return;
        }
        meta.setConcluida(false);
        metaRepository.save(meta);

        usuario.setPontosTotais(Math.max(0, usuario.getPontosTotais() - meta.getPontos()));
        usuarioService.salvarUsuario(usuario);
    }

    @Override
    public void excluirMeta(Long id, Usuario usuario) {
        Meta meta = getMetaById(id);
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta meta não pertence ao usuário logado.");
        }
        metaRepository.deleteById(id);
    }

}
