# AGENTS.md — Convenções para agentes de IA (Oficina Desafio TI)

Este arquivo é lido pelo OpenCode antes de qualquer alteração. Copie-o para a raiz do projeto Flutter e para a raiz do projeto Spring Boot, junto com a pasta `contratos/`.

## 1. Fonte de verdade
- Os contratos em `contratos/` (`contrato-api.md` e `contrato-app.md`) são a única referência para rotas, campos JSON, status HTTP, regras, textos, IDs, alternativas e gabarito.
- Não invente nada que não esteja nos contratos.
- Se o pedido entrar em conflito com um contrato, pare, explique o conflito em português e aguarde orientação. Não resolva sozinho.

## 2. Projetos já existentes
- Não crie projetos novos.
- Não execute `flutter create`, Spring Initializr, scaffolding ou comandos equivalentes.
- Não substitua a estrutura inicial, `pubspec.yaml`, `pom.xml`, `mvnw` ou arquivos de configuração sem pedido explícito.
- Trabalhe somente dentro da pasta do projeto aberto.
- Adicione dependências apenas quando o prompt pedir (por exemplo, `http`).

## 3. Escopo
- Implemente somente o que o prompt atual pede.
- Não adicione funcionalidades fora do contrato: ranking no app, banco de dados, autenticação, cadastro, WebSocket, animações complexas ou telas extras.
- Mantenha o código pequeno e legível para iniciantes. Evite abstrações, padrões de arquitetura e bibliotecas que não foram pedidos.
- Não altere telas ou o visual que já funcionam, a menos que o prompt peça.

## 4. Regras de segurança do jogo
- Na etapa local, o gabarito fica no Flutter. A partir da etapa de integração, o Flutter não pode conter gabarito nem decidir acertos.
- Na API, a validação e a pontuação acontecem somente no backend. Nunca aceite pontuação enviada pelo app.
- Não exponha o gabarito em nenhum endpoint de perguntas.

## 5. Como trabalhar
- Antes de editar, leia os arquivos relevantes do projeto.
- Faça alterações em passos pequenos e explique cada um.
- Não apague arquivos existentes sem perguntar.
- Não faça commit, push ou alteração de repositório remoto sem pedido.
- Não altere `AGENTS.md`, a pasta `contratos/` nem os prompts.

## 6. O que entregar ao aluno ao terminar
- Lista dos arquivos criados e alterados, com uma frase explicando cada um.
- Como executar o projeto.
- Como testar, com os comandos ou passos exatos.
- Se algo do contrato não foi implementado, diga o que faltou e por quê.
- Responda sempre em português, em linguagem simples.

## 7. Convenções de uso de IA na oficina
- A IA gera o código; a pessoa lê, executa, testa e explica.
- O aluno deve conseguir explicar em voz alta cada trecho que a IA escreveu. Se não consegue, peça uma explicação antes de seguir.
- Só avance para a próxima etapa depois de cumprir o checkpoint da etapa atual, com o teste indicado.
- Quando o resultado não estiver de acordo com o contrato, peça correção com a mensagem de erro ou o comportamento observado. Não aceite "parece correto" como validação.
- Não use IA para produzir código que a própria pessoa precisará defender sem revisar.
- Se a instituição tiver regra própria sobre uso de IA em avaliações, ela prevalece sobre este documento.
