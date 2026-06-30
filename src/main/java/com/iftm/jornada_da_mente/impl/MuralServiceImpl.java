package com.iftm.jornada_da_mente.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iftm.jornada_da_mente.model.CurtidaMural;
import com.iftm.jornada_da_mente.model.MensagemMural;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.repository.CurtidaMuralRepository;
import com.iftm.jornada_da_mente.repository.MensagemMuralRepository;
import com.iftm.jornada_da_mente.service.IMuralService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Service
public class MuralServiceImpl implements IMuralService {

    @Autowired
    private MensagemMuralRepository mensagemRepository;

    @Autowired
    private CurtidaMuralRepository curtidaRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public void criarMensagensPadraoSeNecessario() {
        if (mensagemRepository.count() > 0) {
            return;
        }

        Usuario mariana = obterOuCriarUsuarioDemo("mariana.costa@jornadadamente.demo", "Mariana Costa");
        Usuario lucas = obterOuCriarUsuarioDemo("lucas.ferreira@jornadadamente.demo", "Lucas Ferreira");

        salvarMensagemComData(mariana, "Você chegou até aqui, já é uma vitória", LocalDateTime.now().minusHours(2));
        salvarMensagemComData(lucas, "Não desista. Estamos juntos nessa jornada", LocalDateTime.now().minusHours(4));
    }

    private Usuario obterOuCriarUsuarioDemo(String email, String nome) {
        if (usuarioService.emailJaCadastrado(email)) {
            return usuarioService.getUsuarioByEmail(email);
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("demo123");
        return usuarioService.registrarUsuario(usuario);
    }

    private void salvarMensagemComData(Usuario autor, String texto, LocalDateTime dataCriacao) {
        MensagemMural mensagem = new MensagemMural();
        mensagem.setAutor(autor);
        mensagem.setTexto(texto);
        mensagem.setDataCriacao(dataCriacao);
        mensagemRepository.save(mensagem);
    }

    @Override
    public List<MensagemMural> getMensagens() {
        return mensagemRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Override
    public MensagemMural publicarMensagem(String texto, Usuario autor) {
        MensagemMural mensagem = new MensagemMural();
        mensagem.setTexto(texto.trim());
        mensagem.setAutor(autor);
        return mensagemRepository.save(mensagem);
    }

    @Override
    public void alternarCurtida(Long mensagemId, Usuario usuario) {
        MensagemMural mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada: " + mensagemId));

        curtidaRepository.findByMensagemAndUsuario(mensagem, usuario)
                .ifPresentOrElse(
                        curtidaRepository::delete,
                        () -> {
                            CurtidaMural curtida = new CurtidaMural();
                            curtida.setMensagem(mensagem);
                            curtida.setUsuario(usuario);
                            curtidaRepository.save(curtida);
                        });
    }

    @Override
    public void excluirMensagem(Long mensagemId, Usuario usuario) {
        MensagemMural mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada: " + mensagemId));
        if (!mensagem.getAutor().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você só pode excluir suas próprias mensagens.");
        }
        curtidaRepository.deleteAll(curtidaRepository.findByMensagem(mensagem));
        mensagemRepository.delete(mensagem);
    }

    @Override
    public long getCurtidasCount(MensagemMural mensagem) {
        return curtidaRepository.countByMensagem(mensagem);
    }

    @Override
    public boolean usuarioCurtiu(MensagemMural mensagem, Usuario usuario) {
        return curtidaRepository.findByMensagemAndUsuario(mensagem, usuario).isPresent();
    }
}
