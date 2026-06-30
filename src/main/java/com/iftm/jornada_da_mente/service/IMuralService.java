package com.iftm.jornada_da_mente.service;

import java.util.List;

import com.iftm.jornada_da_mente.model.MensagemMural;
import com.iftm.jornada_da_mente.model.Usuario;

public interface IMuralService {
    void criarMensagensPadraoSeNecessario();
    List<MensagemMural> getMensagens();
    MensagemMural publicarMensagem(String texto, Usuario autor);
    void alternarCurtida(Long mensagemId, Usuario usuario);
    void excluirMensagem(Long mensagemId, Usuario usuario);
    long getCurtidasCount(MensagemMural mensagem);
    boolean usuarioCurtiu(MensagemMural mensagem, Usuario usuario);
}
