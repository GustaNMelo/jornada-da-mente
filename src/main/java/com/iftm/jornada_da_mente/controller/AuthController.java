package com.iftm.jornada_da_mente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IConquistaService;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IMetaService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class AuthController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private IDesafioService desafioService;

    @Autowired
    private IConquistaService conquistaService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro/salvar")
    public String salvarCadastro(@ModelAttribute Usuario usuario, Model model, RedirectAttributes redirect) {
        if (usuarioService.emailJaCadastrado(usuario.getEmail())) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", "Já existe uma conta cadastrada com esse e-mail.");
            return "auth/cadastro";
        }
        Usuario novoUsuario = usuarioService.registrarUsuario(usuario);

        // semente inicial para o usuário não começar com a tela vazia
        metaService.criarMetasPadrao(novoUsuario);
        conquistaService.concederConquistaIniciante(novoUsuario);
        desafioService.getDesafioDestaque().ifPresent(desafio ->
                desafioService.getOrCriarParticipacao(desafio, novoUsuario));

        redirect.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça login para continuar.");
        return "redirect:/login";
    }
}
