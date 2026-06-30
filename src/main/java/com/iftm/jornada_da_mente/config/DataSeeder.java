package com.iftm.jornada_da_mente.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.iftm.jornada_da_mente.service.IConquistaService;
import com.iftm.jornada_da_mente.service.IDesafioService;
import com.iftm.jornada_da_mente.service.IMuralService;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private IDesafioService desafioService;

    @Autowired
    private IConquistaService conquistaService;

    @Autowired
    private IMuralService muralService;

    @Override
    public void run(String... args) {
        desafioService.criarDesafioPadraoSeNecessario();
        conquistaService.criarConquistasPadraoSeNecessario();
        muralService.criarMensagensPadraoSeNecessario();
    }
}
