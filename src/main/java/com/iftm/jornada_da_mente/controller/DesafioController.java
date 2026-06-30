package com.iftm.jornada_da_mente.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iftm.jornada_da_mente.model.Desafio;
import com.iftm.jornada_da_mente.model.ParticipacaoDesafio;
import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

@Controller
public class DesafioController {

    @Autowired
    private IDesafioService desafioService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/desafios")
    public String verDesafio(@RequestParam(required = false) Long id, Authentication authentication, Model model,
                              RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        List<Desafio> meusDesafios = desafioService.getDesafiosDoUsuario(usuario);

        Desafio desafio = null;
        if (id != null) {
            desafio = desafioService.getDesafioById(id);
            if (!desafioService.usuarioParticipaAceito(desafio, usuario)) {
                redirect.addFlashAttribute("erro", "Você não faz parte desse desafio.");
                return "redirect:/desafios";
            }
        } else if (!meusDesafios.isEmpty()) {
            desafio = meusDesafios.get(0);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("todosDesafios", meusDesafios);
        model.addAttribute("desafio", desafio);

        if (desafio != null) {
            ParticipacaoDesafio minhaParticipacao = desafioService.getOrCriarParticipacao(desafio, usuario);
            List<ParticipacaoDesafio> ranking = desafioService.getRanking(desafio);
            boolean jaRegistrouHoje = LocalDate.now().equals(minhaParticipacao.getUltimoRegistro());

            int percentual = desafio.getDuracaoDias() != null && desafio.getDuracaoDias() > 0
                    ? Math.min(100, minhaParticipacao.getDiasCompletados() * 100 / desafio.getDuracaoDias())
                    : 0;

            model.addAttribute("minhaParticipacao", minhaParticipacao);
            model.addAttribute("ranking", ranking);
            model.addAttribute("totalParticipantes", ranking.size());
            model.addAttribute("jaRegistrouHoje", jaRegistrouHoje);
            model.addAttribute("progressoPercentual", percentual);
            model.addAttribute("souCriador", desafio.getCriador() != null && desafio.getCriador().getId().equals(usuario.getId()));
        }

        return "desafios/view";
    }

    @GetMapping("/desafios/novo")
    public String novoForm(Model model) {
        model.addAttribute("desafio", new Desafio());
        return "desafios/form";
    }

    @PostMapping("/desafios/salvar")
    public String salvar(@ModelAttribute Desafio desafio, Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        Desafio salvo = desafioService.criarDesafioComCriador(desafio, usuario);
        return "redirect:/desafios?id=" + salvo.getId();
    }

    @PostMapping("/desafios/{id}/registrar-hoje")
    public String registrarHoje(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        Desafio desafio = desafioService.getDesafioById(id);

        List<ParticipacaoDesafio> rankingAntes = desafioService.getRanking(desafio);
        boolean eraPrimeiro = !rankingAntes.isEmpty()
                && rankingAntes.get(0).getUsuario().getId().equals(usuario.getId());

        desafioService.registrarProgressoHoje(id, usuario);

        List<ParticipacaoDesafio> rankingDepois = desafioService.getRanking(desafio);
        boolean ePrimeiroAgora = !rankingDepois.isEmpty()
                && rankingDepois.get(0).getUsuario().getId().equals(usuario.getId());
        boolean virouPrimeiro = ePrimeiroAgora && !eraPrimeiro;

        redirect.addFlashAttribute("sucesso", "Progresso de hoje registrado! +30 pts");
        redirect.addFlashAttribute("participanteAlteradoId", usuario.getId());
        redirect.addFlashAttribute("tocarSom", true);
        if (virouPrimeiro) {
            redirect.addFlashAttribute("celebrarParticipanteId", usuario.getId());
        }
        return "redirect:/desafios?id=" + id;
    }

    @PostMapping("/desafios/{id}/simular-dia")
    public String simularNovoDia(@PathVariable Long id, Authentication authentication, RedirectAttributes redirect,
                                  HttpSession session) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        desafioService.simularNovoDia(id, usuario);

        Integer diasSimulados = (Integer) session.getAttribute(GlobalModelAttributes.SESSION_DIAS_SIMULADOS);
        if (diasSimulados == null) {
            diasSimulados = 0;
        }
        session.setAttribute(GlobalModelAttributes.SESSION_DIAS_SIMULADOS, diasSimulados + 1);

        redirect.addFlashAttribute("sucesso", "Um novo dia começou (simulação). Você já pode registrar hoje novamente.");
        return "redirect:/desafios?id=" + id;
    }

    @GetMapping("/desafios/{id}/convidar")
    public String convidarForm(@PathVariable Long id, Authentication authentication, Model model,
                                RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        Desafio desafio = desafioService.getDesafioById(id);

        if (!desafioService.usuarioECriador(desafio, usuario)) {
            redirect.addFlashAttribute("erro", "Apenas o criador do desafio pode convidar novos participantes.");
            return "redirect:/desafios?id=" + id;
        }

        model.addAttribute("desafio", desafio);
        model.addAttribute("usuariosConvidaveis", desafioService.getUsuariosConvidaveis(desafio, usuario));
        return "desafios/convidar";
    }

    @PostMapping("/desafios/{id}/convidar")
    public String convidar(@PathVariable Long id, @RequestParam Long usuarioId,
                            Authentication authentication, RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        try {
            desafioService.convidarUsuario(id, usuarioId, usuario);
            redirect.addFlashAttribute("sucesso", "Convite enviado!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/desafios?id=" + id;
    }

    @GetMapping("/desafios/convites")
    public String convites(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        model.addAttribute("usuario", usuario);
        model.addAttribute("convites", desafioService.getConvitesPendentes(usuario));
        return "desafios/convites";
    }

    @PostMapping("/desafios/convites/{participacaoId}/aceitar")
    public String aceitarConvite(@PathVariable Long participacaoId, Authentication authentication,
                                  RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        desafioService.aceitarConvite(participacaoId, usuario);
        redirect.addFlashAttribute("sucesso", "Convite aceito! Você já faz parte do desafio.");
        return "redirect:/desafios/convites";
    }

    @PostMapping("/desafios/convites/{participacaoId}/recusar")
    public String recusarConvite(@PathVariable Long participacaoId, Authentication authentication,
                                  RedirectAttributes redirect) {
        Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
        desafioService.recusarConvite(participacaoId, usuario);
        redirect.addFlashAttribute("sucesso", "Convite recusado.");
        return "redirect:/desafios/convites";
    }
}
