package com.iftm.jornada_da_mente.controller;

import java.time.LocalDate;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.iftm.jornada_da_mente.model.Usuario;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IUsuarioService;

/**
 * Atributos disponíveis em todas as páginas autenticadas: o número de
 * convites de desafio pendentes (usado para o aviso no sininho/menu lateral)
 * e a data "atual" considerando a simulação de avanço de dias (botão
 * "Simular novo dia" em Metas e Desafios). A simulação é guardada apenas na
 * sessão do usuário (não altera nada no banco), através do contador
 * "diasSimulados".
 */
@ControllerAdvice
public class GlobalModelAttributes {

    public static final String SESSION_DIAS_SIMULADOS = "diasSimulados";

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IDesafioService desafioService;

    @ModelAttribute("convitesPendentesCount")
    public long convitesPendentesCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            return 0;
        }
        try {
            Usuario usuario = usuarioService.getUsuarioByEmail(authentication.getName());
            return desafioService.getConvitesPendentes(usuario).size();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    @ModelAttribute("dataAtual")
    public LocalDate dataAtual(HttpSession session) {
        Integer diasSimulados = (Integer) session.getAttribute(SESSION_DIAS_SIMULADOS);
        if (diasSimulados == null) {
            diasSimulados = 0;
        }
        return LocalDate.now().plusDays(diasSimulados);
    }
}
