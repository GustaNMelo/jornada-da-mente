package com.iftm.jornada_da_mente.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iftm.jornada_da_mente.model.Meta;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IMetaService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class MetaController {

    @Autowired
    private IMetaService metaService;

    @Autowired
    private IUsuarioService usuarioService;

    private LocalDate getHojeSimulado(HttpSession session) {
        Integer diasSimulados = (Integer) session.getAttribute(GlobalModelAttributes.SESSION_DIAS_SIMULADOS);
        if (diasSimulados == null) {
            diasSimulados = 0;
        }
        return LocalDate.now().plusDays(diasSimulados);
    }

    @GetMapping("/metas")
    public String listarMetas(Authentication authentication, Model model, HttpSession session) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        List<Meta> metas = metaService.getMetasDoUsuario(usuario);

        // "hoje" é simulável apenas para fins de demonstração (botão "Simular novo dia"),
        // através de um contador guardado na sessão. As datas das próprias metas
        // (dataReferencia) nunca são alteradas por essa simulação.
        LocalDate hoje = getHojeSimulado(session);
        LocalDate amanha = hoje.plusDays(1);

        List<Meta> metasHoje = metas.stream()
                .filter(m -> m.getDataReferencia().equals(hoje))
                .sorted(Comparator.comparing(Meta::isConcluida).reversed())
                .toList();
        List<Meta> metasAmanha = metas.stream()
                .filter(m -> m.getDataReferencia().equals(amanha))
                .toList();
        List<Meta> metasFuturas = metas.stream()
                .filter(m -> m.getDataReferencia().isAfter(amanha))
                .sorted(Comparator.comparing(Meta::getDataReferencia))
                .toList();
        List<Meta> metasAtrasadas = metas.stream()
                .filter(m -> m.getDataReferencia().isBefore(hoje) && !m.isConcluida())
                .toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("hoje", hoje);
        model.addAttribute("metasHoje", metasHoje);
        model.addAttribute("metasAmanha", metasAmanha);
        model.addAttribute("metasFuturas", metasFuturas);
        model.addAttribute("metasAtrasadas", metasAtrasadas);
        return "metas/list";
    }

    @GetMapping("/metas/nova")
    public String showCreateForm(Model model, HttpSession session) {
        Meta meta = new Meta();
        meta.setDataReferencia(getHojeSimulado(session));
        model.addAttribute("meta", meta);
        return "metas/form";
    }

    @PostMapping("/metas/salvar")
    public String salvarMeta(@ModelAttribute Meta meta, Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        meta.setUsuario(usuario);
        meta.setConcluida(false);
        metaService.salvarMeta(meta);
        return "redirect:/metas";
    }

    @PostMapping("/metas/{id}/concluir")
    public String concluirMeta(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        metaService.concluirMeta(id, usuario);
        redirect.addFlashAttribute("metaAlteradaId", id);
        redirect.addFlashAttribute("tocarSom", true);
        return "redirect:/metas";
    }

    @PostMapping("/metas/{id}/desmarcar")
    public String desmarcarMeta(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        metaService.desmarcarMeta(id, usuario);
        redirect.addFlashAttribute("metaAlteradaId", id);
        return "redirect:/metas";
    }

    @PostMapping("/metas/{id}/excluir")
    public String excluirMeta(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        try {
            metaService.excluirMeta(id, usuario);
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/metas";
    }

    @PostMapping("/metas/simular-dia")
    public String simularNovoDia(RedirectAttributes redirect, HttpSession session) {
        Integer diasSimulados = (Integer) session.getAttribute(GlobalModelAttributes.SESSION_DIAS_SIMULADOS);
        if (diasSimulados == null) {
            diasSimulados = 0;
        }
        session.setAttribute(GlobalModelAttributes.SESSION_DIAS_SIMULADOS, diasSimulados + 1);
        redirect.addFlashAttribute("sucesso", "Um novo dia começou (simulação).");
        return "redirect:/metas";
    }
}
