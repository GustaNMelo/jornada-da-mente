package com.iftm.jornada_da_mente.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.iftm.jornada_da_mente.exception.UsuarioNaoEncontradoException;

/**
 * Como o banco H2 é em memória, toda vez que a aplicação é reiniciada os
 * dados são apagados, mas o cookie de sessão do navegador pode continuar
 * válido (sessão antiga). Isso fazia o sistema tentar carregar um usuário
 * autenticado que não existe mais no banco, gerando erro 500.
 *
 * Este handler intercepta esse caso, encerra a sessão antiga e manda o
 * usuário de volta para o login com um aviso amigável.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ModelAndView handleUsuarioNaoEncontrado(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/login?sessaoExpirada");
    }
}
