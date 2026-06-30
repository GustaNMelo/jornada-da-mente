package com.iftm.jornada_da_mente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.iftm.jornada_da_mente.model.Desafio;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IConquistaService;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IMetaService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class HomeController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private IDesafioService desafioService;

    @Autowired
    private IConquistaService conquistaService;

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());

        model.addAttribute("usuario", usuario);
        model.addAttribute("metasAtivas", metaService.getMetasAtivasCount(usuario));
        model.addAttribute("conquistasCount", conquistaService.countByUsuario(usuario));
        model.addAttribute("proximaMeta", metaService.getProximaMetaPendente(usuario).orElse(null));

        List<Desafio> meusDesafios = desafioService.getDesafiosDoUsuario(usuario);
        model.addAttribute("desafioDestaque", meusDesafios.isEmpty() ? null : meusDesafios.get(0));

        return "home";
    }

    @PostMapping("/home/metas/{id}/concluir")
    public String concluirMetaNaHome(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        metaService.concluirMeta(id, usuario);
        return "redirect:/home";
    }

    @PostMapping("/home/desafios/{id}/concluir-hoje")
    public String concluirDesafioNaHome(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        desafioService.registrarProgressoHoje(id, usuario);
        return "redirect:/home";
    }
}
