# Jornada da Mente

Sistema web para acompanhamento de hábitos saudáveis com gamificação, desenvolvido como Trabalho de Conclusão de Disciplina para a disciplina de Experiência do Usuário — ADS 5º Período, IFTM Campus Patrocínio.

## Funcionalidades

- **Metas pessoais** — cadastro, acompanhamento por prazo (atrasadas, hoje, amanhã, futuras) e conclusão com feedback sonoro e visual
- **Desafios em equipe** — criação de desafios personalizados, convite de participantes e registro diário de progresso com ranking
- **Mural de apoio** — mural comunitário com mensagens de incentivo e curtidas
- **Gamificação** — sistema de pontos, níveis e conquistas desbloqueáveis por marcos
- **Perfil do usuário** — estatísticas pessoais, histórico de conquistas e dias seguidos
- **Modo escuro** — alternância de tema persistida na sessão

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Security
- Spring Data JPA
- Thymeleaf + Thymeleaf Extras Security
- H2 (banco em memória para desenvolvimento)
- CSS Grid / Flexbox (sem frameworks externos)

## Como executar

**Pré-requisitos:** Java 21+, Maven

```bash
# Clone o repositório
git clone https://github.com/GustaNMelo/jornada-da-mente.git
cd jornada-da-mente

# Execute
./mvnw spring-boot:run
```

Acesse em: [http://localhost:8080](http://localhost:8080)

### Credenciais padrão

| Usuário | Senha |
|---------|-------|
| admin   | admin |

> Para popular o banco com dados de exemplo, execute o script `seed-data.sql` após subir a aplicação.

## Estrutura do projeto

```
src/
├── main/
│   ├── java/com/iftm/jornadadamente/
│   │   ├── config/        # Configuração de segurança
│   │   ├── controller/    # Controllers MVC
│   │   ├── model/         # Entidades JPA
│   │   ├── repository/    # Repositórios Spring Data
│   │   └── service/       # Regras de negócio
│   └── resources/
│       ├── templates/     # Templates Thymeleaf
│       └── static/        # CSS e assets
```

## Autor

Gustavo Nunes Melo — IFTM Campus Patrocínio, 2026
