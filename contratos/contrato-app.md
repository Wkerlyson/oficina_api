# Contrato do aplicativo — Desafio TI

Define o comportamento esperado do Flutter. O agente não deve inventar funcionalidades.

## Objetivo e fluxo
Quiz mobile sobre tecnologia para competição entre estudantes.
1. Início: campo de apelido e botão começar.
2. Pergunta: enunciado, quatro alternativas, progresso e pontuação.
3. Feedback: informar acerto/erro e explicação.
4. Resultado: apelido, pontuação, quantidade de acertos e botão jogar novamente.

Não criar login, cadastro, perfil, histórico ou ranking dentro do app.

## Regras
- Exatamente três perguntas e quatro alternativas por pergunta.
- Uma alternativa por pergunta.
- Acerto vale 100 pontos; erro vale zero.
- Após responder, não pode alterar a alternativa.
- Mostrar feedback antes de avançar.
- Ao terminar a terceira pergunta, mostrar resultado final.

## Etapa 1 — modo local
- Perguntas, alternativas, gabarito e explicações hardcoded no Flutter.
- Sem HTTP; funcionar sem internet.
- Usar os mesmos IDs, enunciados, alternativas e gabarito do `contrato-api.md`.

## Etapa 2 — modo API
- Buscar perguntas em `GET /api/perguntas`.
- Enviar cada resposta em `POST /api/respostas`.
- Remover gabarito local: Flutter não decide se acertou.
- Atualizar pontuação com `pontosGanhos` e `pontuacaoTotal` retornados.
- Mostrar `explicacao` retornada pelo backend.
- Enviar o mesmo apelido em todas as respostas.

## URL base
Manter a URL em um único local de fácil alteração:
- Emulador Android acessando API no computador: `http://10.0.2.2:8080`
- Celular físico: `http://IP-DO-COMPUTADOR:8080`
- Competição: URL informada pelo instrutor.

Não espalhar a URL por telas ou serviços.

## Estados e erros
- Mostrar carregamento ao buscar perguntas.
- Impedir envio repetido enquanto uma resposta está sendo enviada.
- Apresentar mensagem amigável se API estiver indisponível.
- Tratar erros HTTP sem encerrar o app.
- Não avançar se o envio falhar.
- Permitir tentar novamente em falha de rede sem duplicar resposta já aceita; tratar HTTP 409 como resposta já registrada.

## Fora do escopo
Sem ranking no Flutter, autenticação, banco local, cadastro, notificações, animações complexas ou funcionalidades não descritas neste contrato.
