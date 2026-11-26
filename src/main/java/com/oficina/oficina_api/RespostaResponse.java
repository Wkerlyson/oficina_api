package com.oficina.oficina_api;

public record RespostaResponse(
        String jogador,
        String perguntaId,
        boolean correta,
        int pontosGanhos,
        int pontuacaoTotal,
        String explicacao) {
}
