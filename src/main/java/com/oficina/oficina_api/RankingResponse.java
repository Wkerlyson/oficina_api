package com.oficina.oficina_api;

public record RankingResponse(
        int posicao,
        String jogador,
        int pontuacao,
        int acertos,
        int respondidas,
        boolean concluido,
        Long tempoConclusaoMs) {
}
