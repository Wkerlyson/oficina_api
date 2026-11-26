package com.oficina.oficina_api;

public record ErroResponse(
        String erro,
        boolean correta,
        int pontosGanhos,
        int pontuacaoTotal) {
}
