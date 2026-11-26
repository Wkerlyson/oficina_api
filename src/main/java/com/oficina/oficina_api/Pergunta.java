package com.oficina.oficina_api;

import java.util.List;

public record Pergunta(String id, String enunciado, List<Alternativa> alternativas) {
}
