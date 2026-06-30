package com.iftm.jornada_da_mente.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iftm.jornada_da_mente.model.Desafio;
import com.iftm.jornada_da_mente.model.ParticipacaoDesafio;
import com.iftm.jornada_da_mente.model.StatusParticipacao;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.repository.DesafioRepository;
import com.iftm.jornada_da_mente.repository.ParticipacaoDesafioRepository;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Service
public class DesafioServiceImpl implements IDesafioService {

    @Autowired
    private DesafioRepository desafioRepository;

    @Autowired
    private ParticipacaoDesafioRepository participacaoRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public void criarDesafioPadraoSeNecessario() {
        if (desafioRepository.count() == 0) {
            Desafio desafio = new Desafio();
            desafio.setTitulo("Desafio de hidratação");
            desafio.setDescricao("Beber 2L de água por dia durante 7 dias");
            desafio.setDuracaoDias(7);
            desafioRepository.save(desafio);
        }
    }

    @Override
    public List<Desafio> getAllDesafios() {
        return desafioRepository.findAll();
    }

    @Override
    public Optional<Desafio> getDesafioDestaque() {
        return desafioRepository.findAll().stream().findFirst();
    }

    @Override
    public Desafio getDesafioById(Long id) {
        return desafioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Desafio não encontrado: " + id));
    }

    @Override
    public Desafio criarDesafioComCriador(Desafio desafio, Usuario criador) {
        desafio.setCriador(criador);
        Desafio salvo = desafioRepository.save(desafio);

        ParticipacaoDesafio participacao = new ParticipacaoDesafio();
        participacao.setDesafio(salvo);
        participacao.setUsuario(criador);
        participacao.setDiasCompletados(0);
        participacao.setPontos(0);
        participacao.setStatus(StatusParticipacao.ACEITA);
        participacaoRepository.save(participacao);

        return salvo;
    }

    @Override
    public List<ParticipacaoDesafio> getRanking(Desafio desafio) {
        return participacaoRepository.findByDesafioAndStatusOrderByPontosDesc(desafio, StatusParticipacao.ACEITA);
    }

    @Override
    public ParticipacaoDesafio getOrCriarParticipacao(Desafio desafio, Usuario usuario) {
        return participacaoRepository.findByDesafioAndUsuario(desafio, usuario)
                .orElseGet(() -> {
                    ParticipacaoDesafio participacao = new ParticipacaoDesafio();
                    participacao.setDesafio(desafio);
                    participacao.setUsuario(usuario);
                    participacao.setDiasCompletados(0);
                    participacao.setPontos(0);
                    participacao.setStatus(StatusParticipacao.ACEITA);
                    return participacaoRepository.save(participacao);
                });
    }

    @Override
    public void registrarProgressoHoje(Long desafioId, Usuario usuario) {
        Desafio desafio = getDesafioById(desafioId);
        ParticipacaoDesafio participacao = getOrCriarParticipacao(desafio, usuario);

        if (participacao.getStatus() != StatusParticipacao.ACEITA) {
            throw new RuntimeException("Você ainda não faz parte deste desafio.");
        }

        LocalDate hoje = LocalDate.now();
        if (hoje.equals(participacao.getUltimoRegistro())) {
            return; // já registrou hoje
        }

        participacao.setDiasCompletados(Math.min(desafio.getDuracaoDias(), participacao.getDiasCompletados() + 1));
        participacao.setPontos(participacao.getPontos() + 30);
        participacao.setUltimoRegistro(hoje);
        participacaoRepository.save(participacao);

        usuario.setPontosTotais(usuario.getPontosTotais() + 30);
        usuarioService.salvarUsuario(usuario);
    }

    @Override
    public void simularNovoDia(Long desafioId, Usuario usuario) {
        // Apenas para fins de demonstração: "volta" a data do último registro em 1
        // dia, liberando o botão "Registrar hoje" novamente sem alterar pontos ou
        // dias já completados.
        Desafio desafio = getDesafioById(desafioId);
        ParticipacaoDesafio participacao = getOrCriarParticipacao(desafio, usuario);
        if (participacao.getUltimoRegistro() != null) {
            participacao.setUltimoRegistro(participacao.getUltimoRegistro().minusDays(1));
            participacaoRepository.save(participacao);
        }
    }

    @Override
    public List<Desafio> getDesafiosDoUsuario(Usuario usuario) {
        return participacaoRepository.findByUsuarioAndStatus(usuario, StatusParticipacao.ACEITA)
                .stream()
                .map(ParticipacaoDesafio::getDesafio)
                .sorted(Comparator.comparing(Desafio::getId))
                .toList();
    }

    @Override
    public boolean usuarioParticipaAceito(Desafio desafio, Usuario usuario) {
        return participacaoRepository.findByDesafioAndUsuario(desafio, usuario)
                .filter(p -> p.getStatus() == StatusParticipacao.ACEITA)
                .isPresent();
    }

    @Override
    public boolean usuarioECriador(Desafio desafio, Usuario usuario) {
        return desafio.getCriador() != null && desafio.getCriador().getId().equals(usuario.getId());
    }

    @Override
    public void convidarUsuario(Long desafioId, Long usuarioConvidadoId, Usuario quemConvida) {
        Desafio desafio = getDesafioById(desafioId);

        if (!usuarioECriador(desafio, quemConvida)) {
            throw new RuntimeException("Apenas o criador do desafio pode convidar novos participantes.");
        }

        Usuario convidado = usuarioService.getUsuarioById(usuarioConvidadoId);

        boolean jaExiste = participacaoRepository.findByDesafioAndUsuario(desafio, convidado).isPresent();
        if (jaExiste) {
            return; // já é participante ou já foi convidado
        }

        ParticipacaoDesafio convite = new ParticipacaoDesafio();
        convite.setDesafio(desafio);
        convite.setUsuario(convidado);
        convite.setDiasCompletados(0);
        convite.setPontos(0);
        convite.setStatus(StatusParticipacao.PENDENTE);
        convite.setConvidadoPor(quemConvida);
        participacaoRepository.save(convite);
    }

    @Override
    public List<ParticipacaoDesafio> getConvitesPendentes(Usuario usuario) {
        return participacaoRepository.findByUsuarioAndStatus(usuario, StatusParticipacao.PENDENTE);
    }

    @Override
    public void aceitarConvite(Long participacaoId, Usuario usuario) {
        ParticipacaoDesafio participacao = participacaoRepository.findById(participacaoId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado: " + participacaoId));
        if (!participacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Este convite não pertence ao usuário logado.");
        }
        participacao.setStatus(StatusParticipacao.ACEITA);
        participacaoRepository.save(participacao);
    }

    @Override
    public void recusarConvite(Long participacaoId, Usuario usuario) {
        ParticipacaoDesafio participacao = participacaoRepository.findById(participacaoId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado: " + participacaoId));
        if (!participacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Este convite não pertence ao usuário logado.");
        }
        participacaoRepository.delete(participacao);
    }

    @Override
    public List<Usuario> getUsuariosConvidaveis(Desafio desafio, Usuario quemConvida) {
        List<ParticipacaoDesafio> participacoes = participacaoRepository.findByDesafioOrderByPontosDesc(desafio);
        return usuarioService.getAllUsuarios().stream()
                .filter(u -> !u.getId().equals(quemConvida.getId()))
                .filter(u -> participacoes.stream().noneMatch(p -> p.getUsuario().getId().equals(u.getId())))
                .toList();
    }
}
