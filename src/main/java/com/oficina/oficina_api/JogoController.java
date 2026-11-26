package com.oficina.oficina_api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class JogoController {

    private static final int PONTOS_POR_ACERTO = 100;
    private static final String MSG_CAMPOS_INVALIDOS = "apelido ausente/inválido ou campos obrigatórios ausentes.";
    private static final String MSG_NAO_ENCONTRADO = "pergunta ou alternativa inexistente.";
    private static final String MSG_DUPLICADO = "Este participante já respondeu a esta pergunta.";

    private final List<Pergunta> perguntas = List.of(
            new Pergunta("q1", "Qual estrutura de dados segue a ordem FIFO: o primeiro que entra é o primeiro que sai?", List.of(
                    new Alternativa("a", "Pilha"),
                    new Alternativa("b", "Fila"),
                    new Alternativa("c", "Árvore"),
                    new Alternativa("d", "Grafo"))),
            new Pergunta("q2", "Em Java, qual palavra-chave impede que uma variável receba outro valor depois da atribuição?", List.of(
                    new Alternativa("a", "static"),
                    new Alternativa("b", "final"),
                    new Alternativa("c", "let"),
                    new Alternativa("d", "var"))),
            new Pergunta("q3", "Qual anotação do Spring Boot marca uma classe como controlador de uma API REST?", List.of(
                    new Alternativa("a", "@Entity"),
                    new Alternativa("b", "@Service"),
                    new Alternativa("c", "@RestController"),
                    new Alternativa("d", "@Repository"))),
            new Pergunta("q4", "O que acontece ao acessar a posição 0 de uma lista vazia em Java?", List.of(
                    new Alternativa("a", "Retorna null"),
                    new Alternativa("b", "Retorna 0"),
                    new Alternativa("c", "Lança IndexOutOfBoundsException"),
                    new Alternativa("d", "Cria um novo elemento"))),
            new Pergunta("q5", "Qual comando Git baixa as alterações do repositório remoto e as aplica ao branch atual?", List.of(
                    new Alternativa("a", "git push"),
                    new Alternativa("b", "git pull"),
                    new Alternativa("c", "git commit"),
                    new Alternativa("d", "git clone"))));

    private final Map<String, String> gabarito = Map.of(
            "q1", "b",
            "q2", "b",
            "q3", "c",
            "q4", "c",
            "q5", "b");

    private final Map<String, String> explicacoes = Map.of(
            "q1", "A fila segue a ordem FIFO: o primeiro elemento inserido é o primeiro removido.",
            "q2", "Em Java, a palavra-chave final impede a reatribuição da variável.",
            "q3", "@RestController é usado para criar controladores que respondem com dados, normalmente JSON.",
            "q4", "Uma lista vazia não tem posição 0, por isso o acesso lança IndexOutOfBoundsException.",
            "q5", "git pull busca as alterações remotas e as mescla no branch atual.");

    private final Map<String, Participante> participantes = new HashMap<>();

    private static class Participante {
        final String nome;
        int pontuacao;
        int acertos;
        final Set<String> respondidas = new HashSet<>();
        Long primeiraRespostaMs;
        Long tempoConclusaoMs;

        Participante(String nome) {
            this.nome = nome;
        }
    }

    @GetMapping("/perguntas")
    public Map<String, List<Pergunta>> listarPerguntas() {
        return Map.of("perguntas", perguntas);
    }

    @GetMapping("/ranking")
    public synchronized Map<String, List<RankingResponse>> listarRanking() {
        List<Participante> ordenados = new ArrayList<>(participantes.values());
        ordenados.sort(comparadorRanking());

        List<RankingResponse> ranking = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            Participante p = ordenados.get(i);
            boolean concluido = p.respondidas.size() == perguntas.size();
            ranking.add(new RankingResponse(
                    i + 1,
                    p.nome,
                    p.pontuacao,
                    p.acertos,
                    p.respondidas.size(),
                    concluido,
                    concluido ? p.tempoConclusaoMs : null));
        }
        return Map.of("ranking", ranking);
    }

    // Endpoint auxiliar de teste: limpa o estado em memória sem reiniciar o servidor.
    @GetMapping("/respostas/limpar")
    public synchronized Map<String, Boolean> limparRespostas() {
        participantes.clear();
        return Map.of("ok", true);
    }

    @PostMapping("/respostas")
    public synchronized ResponseEntity<?> responder(@RequestBody(required = false) RespostaRequest request) {
        if (request == null || request.jogador() == null || request.perguntaId() == null || request.alternativaId() == null) {
            return erro(HttpStatus.BAD_REQUEST, MSG_CAMPOS_INVALIDOS, null);
        }

        String jogador = request.jogador().trim();
        if (jogador.isEmpty() || jogador.length() > 20) {
            return erro(HttpStatus.BAD_REQUEST, MSG_CAMPOS_INVALIDOS, jogador);
        }

        Pergunta pergunta = encontrarPergunta(request.perguntaId());
        if (pergunta == null) {
            return erro(HttpStatus.NOT_FOUND, MSG_NAO_ENCONTRADO, jogador);
        }

        boolean alternativaExiste = pergunta.alternativas().stream()
                .anyMatch(a -> a.id().equals(request.alternativaId()));
        if (!alternativaExiste) {
            return erro(HttpStatus.NOT_FOUND, MSG_NAO_ENCONTRADO, jogador);
        }

        String chave = jogador.toLowerCase(Locale.ROOT);
        Participante participante = participantes.computeIfAbsent(chave, k -> new Participante(jogador));
        if (!participante.respondidas.add(request.perguntaId())) {
            return erro(HttpStatus.CONFLICT, MSG_DUPLICADO, jogador);
        }

        long agora = System.currentTimeMillis();
        if (participante.primeiraRespostaMs == null) {
            participante.primeiraRespostaMs = agora;
        }

        boolean correta = gabarito.get(request.perguntaId()).equals(request.alternativaId());
        int pontosGanhos = correta ? PONTOS_POR_ACERTO : 0;
        participante.pontuacao += pontosGanhos;
        if (correta) {
            participante.acertos++;
        }

        if (participante.respondidas.size() == perguntas.size() && participante.tempoConclusaoMs == null) {
            participante.tempoConclusaoMs = agora - participante.primeiraRespostaMs;
        }

        return ResponseEntity.ok(new RespostaResponse(
                jogador,
                request.perguntaId(),
                correta,
                pontosGanhos,
                participante.pontuacao,
                explicacoes.get(request.perguntaId())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoInvalido() {
        return erro(HttpStatus.BAD_REQUEST, MSG_CAMPOS_INVALIDOS, null);
    }

    private Pergunta encontrarPergunta(String id) {
        return perguntas.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Comparator<Participante> comparadorRanking() {
        return Comparator.comparingInt((Participante p) -> p.pontuacao).reversed()
                .thenComparing(Comparator.comparingInt((Participante p) -> p.acertos).reversed())
                .thenComparing(p -> p.respondidas.size() != perguntas.size())
                .thenComparing(p -> p.tempoConclusaoMs, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(p -> p.nome.toLowerCase(Locale.ROOT));
    }

    private ResponseEntity<ErroResponse> erro(HttpStatus status, String mensagem, String jogador) {
        Participante participante = jogador == null ? null
                : participantes.get(jogador.trim().toLowerCase(Locale.ROOT));
        int total = participante == null ? 0 : participante.pontuacao;
        return ResponseEntity.status(status)
                .body(new ErroResponse(mensagem, false, 0, total));
    }
}
