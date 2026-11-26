# Contrato da API — Desafio TI

Fonte de verdade para os agentes de backend e Flutter. A API dos alunos implementa perguntas e respostas. A API do instrutor preserva esses endpoints e acrescenta ranking.

## Regras gerais
- Base path `/api`; JSON UTF-8; porta local `8080`.
- Sem autenticação e sem banco. Três perguntas hardcoded no Java.
- Quatro alternativas por pergunta; cada acerto vale 100 pontos.
- Nunca expor o gabarito em `GET /api/perguntas`.

## Modelo de pergunta pública
```json
{
  "id": "q1",
  "enunciado": "O que significa a sigla HTTP?",
  "alternativas": [
    { "id": "a", "texto": "HyperText Transfer Protocol" },
    { "id": "b", "texto": "High Transfer Text Program" },
    { "id": "c", "texto": "Hyper Transfer Terminal Process" },
    { "id": "d", "texto": "Home Tool Transfer Protocol" }
  ]
}
```
Não incluir `correta`, `alternativaCorreta` ou `gabarito` no JSON público.

## GET /api/perguntas
HTTP 200:
```json
{
  "perguntas": [
    {
      "id": "q1",
      "enunciado": "O que significa a sigla HTTP?",
      "alternativas": [
        { "id": "a", "texto": "HyperText Transfer Protocol" },
        { "id": "b", "texto": "High Transfer Text Program" },
        { "id": "c", "texto": "Hyper Transfer Terminal Process" },
        { "id": "d", "texto": "Home Tool Transfer Protocol" }
      ]
    },
    {
      "id": "q2",
      "enunciado": "Qual formato de instruções a JVM executa?",
      "alternativas": [
        { "id": "a", "texto": "HTML" },
        { "id": "b", "texto": "Java bytecode" },
        { "id": "c", "texto": "CSS" },
        { "id": "d", "texto": "SQL" }
      ]
    },
    {
      "id": "q3",
      "enunciado": "Qual é a principal finalidade de uma API REST?",
      "alternativas": [
        { "id": "a", "texto": "Criar imagens" },
        { "id": "b", "texto": "Gerenciar a memória do celular" },
        { "id": "c", "texto": "Permitir comunicação entre sistemas por HTTP" },
        { "id": "d", "texto": "Substituir o sistema operacional" }
      ]
    }
  ]
}
```
A ordem de perguntas e alternativas é estável.

## POST /api/respostas
Requisição:
```json
{
  "jogador": "Ana",
  "perguntaId": "q1",
  "alternativaId": "a"
}
```
`jogador`: apelido não vazio, até 20 caracteres, aplicar trim. `perguntaId` e `alternativaId` devem existir.

Resposta HTTP 200:
```json
{
  "jogador": "Ana",
  "perguntaId": "q1",
  "correta": true,
  "pontosGanhos": 100,
  "pontuacaoTotal": 100,
  "explicacao": "HTTP significa HyperText Transfer Protocol."
}
```
Para erro, `correta=false`, `pontosGanhos=0`; `pontuacaoTotal` contém o acumulado.

### Erros
- `400 Bad Request`: apelido ausente/inválido ou campos obrigatórios ausentes.
- `404 Not Found`: pergunta ou alternativa inexistente.
- `409 Conflict`: participante já respondeu à pergunta.

Exemplo:
```json
{ "erro": "Este participante já respondeu a esta pergunta." }
```

### Regras
1. Gabarito verificado exclusivamente no backend.
2. Cada participante responde cada pergunta uma única vez.
3. Apelido normalizado com trim e comparação sem diferenciar maiúsculas/minúsculas.
4. Repetição retorna 409 e não soma pontos.
5. Estado mantido em memória.
6. Não há endpoint para iniciar/finalizar partida.
7. Nunca aceitar pontuação enviada pelo app.

## GET /api/ranking — somente API do instrutor
Resposta HTTP 200:
```json
{
  "ranking": [
    {
      "posicao": 1,
      "jogador": "Ana",
      "pontuacao": 300,
      "acertos": 3,
      "respondidas": 3,
      "concluido": true,
      "tempoConclusaoMs": 18400
    },
    {
      "posicao": 2,
      "jogador": "Bruno",
      "pontuacao": 200,
      "acertos": 2,
      "respondidas": 3,
      "concluido": true,
      "tempoConclusaoMs": 23100
    }
  ]
}
```
Ordenação: maior pontuação; maior número de acertos; concluídos antes dos não concluídos; menor tempo entre os concluídos; apelido alfabético sem diferenciar maiúsculas/minúsculas. `tempoConclusaoMs` é `null` antes de responder às três perguntas. Medir no servidor, da primeira à terceira resposta recebida.

## Perguntas e gabarito
| ID | Pergunta | Correta | Pontos |
|---|---|---|---:|
| q1 | O que significa a sigla HTTP? | a | 100 |
| q2 | Qual formato de instruções a JVM executa? | b | 100 |
| q3 | Qual é a principal finalidade de uma API REST? | c | 100 |

Explicações: q1 HTTP significa HyperText Transfer Protocol; q2 a JVM executa bytecode Java; q3 APIs REST permitem comunicação entre sistemas usando recursos e métodos HTTP.

## Compatibilidade
A API do instrutor deve preservar exatamente os endpoints, campos JSON, validações e formatos de erro de perguntas e respostas. Pode acrescentar `GET /api/ranking` e a página `/ranking`, sem exigir mudança no app.
