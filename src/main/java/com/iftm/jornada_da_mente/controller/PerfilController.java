package com.iftm.jornada_da_mente.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.iftm.jornada_da_mente.model.UsuarioConquista;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IConquistaService;
import com.iftm.jornada_da_mente.service.IMetaService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class PerfilController {

    private static final DateTimeFormatter FORMATO_DATA_CADASTRO =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private IConquistaService conquistaService;

    @GetMapping("/perfil")
    public String verPerfil(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        List<UsuarioConquista> conquistas = conquistaService.getConquistasDoUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("membroDesde", usuario.getDataCadastro().format(FORMATO_DATA_CADASTRO));
        model.addAttribute("metasCompletadas", metaService.getMetasCompletadasCount(usuario));
        model.addAttribute("conquistas", conquistas);
        model.addAttribute("conquistasCount", conquistas.size());
        return "perfil";
    }
}
