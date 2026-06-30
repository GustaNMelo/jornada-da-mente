package com.iftm.jornada_da_mente.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iftm.jornada_da_mente.model.MensagemMural;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IMuralService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class MuralController {

    @Autowired
    private IMuralService muralService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/mural")
    public String listarMural(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        List<MensagemMural> mensagens = muralService.getMensagens();

        Map<Long, Long> curtidasPorMensagem = new HashMap<>();
        Map<Long, Boolean> curtidoPorMim = new HashMap<>();
        Map<Long, String> tempoRelativo = new HashMap<>();

        for (MensagemMural mensagem : mensagens) {
            curtidasPorMensagem.put(mensagem.getId(), muralService.getCurtidasCount(mensagem));
            curtidoPorMim.put(mensagem.getId(), muralService.usuarioCurtiu(mensagem, usuario));
            boolean ehAutor = mensagem.getAutor().getId().equals(usuario.getId());
            tempoRelativo.put(mensagem.getId(), formatarTempoRelativo(mensagem.getDataCriacao(), ehAutor));
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("mensagens", mensagens);
        model.addAttribute("curtidasPorMensagem", curtidasPorMensagem);
        model.addAttribute("curtidoPorMim", curtidoPorMim);
        model.addAttribute("tempoRelativo", tempoRelativo);
        return "mural/list";
    }

    @PostMapping("/mural/publicar")
    public String publicarMensagem(@RequestParam String texto, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        if (texto != null && !texto.trim().isEmpty()) {
            muralService.publicarMensagem(texto, usuario);
        }
        return "redirect:/mural";
    }

    @PostMapping("/mural/{id}/curtir")
    public String curtirMensagem(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        muralService.alternarCurtida(id, usuario);
        return "redirect:/mural";
    }

    @PostMapping("/mural/{id}/excluir")
    public String excluirMensagem(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        try {
            muralService.excluirMensagem(id, usuario);
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/mural";
    }

    private String formatarTempoRelativo(LocalDateTime dataCriacao, boolean ehAutor) {
        long minutos = Duration.between(dataCriacao, LocalDateTime.now()).toMinutes();
        String tempo;
        if (minutos < 1) {
            tempo = "Agora";
        } else if (minutos < 60) {
            tempo = "Há " + minutos + " min";
        } else if (minutos < 60 * 24) {
            long horas = minutos / 60;
            tempo = "Há " + horas + (horas == 1 ? " hora" : " horas");
        } else {
            long dias = minutos / (60 * 24);
            tempo = "Há " + dias + (dias == 1 ? " dia" : " dias");
        }
        return ehAutor ? tempo + " (você)" : tempo;
    }
}
