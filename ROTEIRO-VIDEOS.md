# Roteiro dos Videos - Digi-SUS

Guia completo para gravacao dos dois videos do hackathon.

---

## Video 1: Pitch da Ideia (maximo 8 minutos)

### BLOCO 1 - Introducao (1 minuto)

**[Tela: slide com nome do projeto e equipe]**

> "Ola! Somos a equipe [NOME DA EQUIPE] e vamos apresentar o Digi-SUS - uma plataforma digital para modernizar o atendimento no Sistema Unico de Saude."

**Apresentar cada membro:**
> "Meu nome eh [NOME], sou responsavel por [FUNCAO]. Comigo estao [NOME - FUNCAO], [NOME - FUNCAO]..."

**Contextualizacao do problema:**
> "O SUS atende mais de 150 milhoes de brasileiros, mas ainda enfrenta desafios criticos no dia a dia: agendamento de consultas feito por telefone ou presencialmente, filas de espera sem priorizacao inteligente, falta de integracao entre setores como triagem, atendimento e exames, e historico medico fragmentado em papel ou sistemas desconectados."

> "O resultado? Pacientes esperando horas sem necessidade, medicos sem acesso ao historico completo, e uma gestao hospitalar ineficiente."

---

### BLOCO 2 - A Solucao (3 minutos)

**[Tela: diagrama de arquitetura do README ou slide simplificado]**

> "O Digi-SUS eh uma plataforma completa que digitaliza todo o fluxo de atendimento do SUS, desde o momento em que o paciente agenda uma consulta ate o registro no historico medico."

**Funcionalidades principais (explicar cada uma brevemente):**

> "**1. Agendamento inteligente:** O medico define sua agenda semanal - por exemplo, segunda-feira das 8h as 12h, em slots de 30 minutos para Cardiologia. O sistema gera automaticamente os horarios disponiveis e o proprio paciente pode se auto-agendar pelo sistema, sem precisar ligar ou ir presencialmente."

> "**2. Triagem com classificacao automatica:** Quando o paciente chega na unidade de saude, o enfermeiro registra os sinais vitais - temperatura, pressao arterial, batimento cardiaco. O sistema classifica automaticamente a prioridade: Emergencia, Urgente, Pouco Urgente ou Nao Urgente. Baseado nisso, uma consulta de urgencia eh criada automaticamente para o paciente."

> "**3. Consulta de encaixe:** Se nao ha horario disponivel no dia, o sistema cria um encaixe - uma consulta extra que nao depende de slot reservado. Isso garante que pacientes com classificacao de emergencia ou urgencia nunca fiquem sem atendimento."

> "**4. Atendimento medico digital:** O medico inicia o atendimento vinculado a consulta, registra anamnese e conduta medica, e pode solicitar exames diretamente do sistema."

> "**5. Exames integrados:** O ciclo completo: medico solicita, administrador configura agendas de exame com multiplas vagas por horario, e o paciente agenda o exame."

> "**6. Historico medico unificado:** Todas as consultas, triagens e atendimentos sao registrados automaticamente no historico do paciente, acessivel via API GraphQL."

**Diferencial:**

> "O grande diferencial do Digi-SUS em relacao a sistemas existentes eh a **integracao total entre todos os setores**. Hoje, muitas unidades de saude usam sistemas separados para agendamento, triagem e prontuario. No Digi-SUS, tudo se comunica em tempo real: a triagem cria consulta automaticamente, o atendimento acessa o historico completo, e o paciente recebe notificacoes a cada etapa. Alem disso, a classificacao de risco automatica garante que pacientes graves sejam priorizados sem depender de avaliacao manual."

---

### BLOCO 3 - Impacto (2 minutos)

**[Tela: slide com beneficios]**

> "O impacto do Digi-SUS se reflete em tres dimensoes:"

**Para o paciente:**
> "- Auto-agendamento elimina filas e ligacoes telefonicas"
> "- Classificacao de risco garante atendimento rapido em casos graves"
> "- Acesso ao proprio historico medico"
> "- Notificacoes sobre agendamentos, cancelamentos e exames"

**Para os profissionais de saude:**
> "- Medico acessa historico completo do paciente antes da consulta"
> "- Enfermeiro tem triagem digital com classificacao automatica"
> "- Agenda medica flexivel e gerenciavel"
> "- Solicitacao de exames integrada ao atendimento"

**Para a gestao hospitalar:**
> "- Reduzao de no-shows com notificacoes automaticas"
> "- Melhor distribuicao de pacientes por especialidade"
> "- Dados estruturados para tomada de decisao"
> "- Menos sobrecarga administrativa na recepcao"

**Caso de uso real:**
> "Imagine a dona Maria, 65 anos, que chega a uma UBS com falta de ar. O enfermeiro registra a triagem: temperatura 38.5, batimento 125 bpm. O sistema classifica como URGENTE e cria automaticamente uma consulta de encaixe com um clinico disponivel. O medico abre o atendimento, ve o historico de triagens e consultas anteriores, atende a dona Maria e solicita um eletrocardiograma pelo sistema. A dona Maria recebe a notificacao do exame agendado. Todo esse fluxo, que antes envolveria filas, papeis e ligacoes, acontece digitalmente em minutos."

---

### BLOCO 4 - Proximos Passos (2 minutos)

**[Tela: slide com roadmap futuro]**

> "O Digi-SUS foi construido como MVP, mas a arquitetura de microsservicos permite evoluir rapidamente. Os proximos passos seriam:"

> "**1. Frontend web e mobile:** Desenvolver interfaces para pacientes (app mobile) e profissionais (painel web), consumindo as APIs que ja estao prontas."

> "**2. Resultado de exames:** Permitir que tecnicos de laboratorio registrem resultados de exames e que o medico acesse diretamente no historico."

> "**3. Prontuario eletronico completo:** Expandir o historico para incluir prescricoes medicas, alergias, medicamentos em uso e laudos."

> "**4. Telemedicina:** Integrar videochamada ao atendimento, permitindo consultas remotas para casos de baixa complexidade."

> "**5. Dashboard de gestao:** Painel com indicadores em tempo real - tempo medio de espera, taxa de ocupacao por especialidade, volume de atendimentos."

> "**6. Integracao com sistemas do SUS:** Conectar com o CADSUS (Cadastro Nacional de Usuarios do SUS) e o SISREG (Sistema de Regulacao)."

> "A arquitetura que escolhemos - microsservicos com mensageria - foi pensada exatamente para permitir essa evolucao sem reescrever o que ja funciona."

**Encerramento:**
> "O Digi-SUS nao eh apenas um sistema de agendamento. Eh uma plataforma que conecta todos os pontos do atendimento de saude publica, priorizando quem mais precisa e dando ferramentas modernas para os profissionais que cuidam da saude do Brasil. Obrigado!"

---
---

## Video 2: MVP Funcionando (maximo 8 minutos)

### Pre-requisitos para a gravacao

1. Todos os servicos rodando via Docker Compose (`docker-compose up --build`)
2. Postman instalado com a collection `Digi-SUS v3 - Testes Completos.postman_collection.json` importada
3. Eureka Dashboard aberto em http://localhost:8761
4. Abrir um terminal para mostrar logs quando necessario (`docker-compose logs -f`)

**Dica:** Faca um dry-run antes de gravar para garantir que tudo funciona. Se der erro em alguma chamada, resete o banco com `docker-compose down -v && docker-compose up --build`.

---

### BLOCO 1 - Visao Geral da Arquitetura (1 minuto)

**[Tela: terminal + Eureka Dashboard]**

> "Antes de demonstrar as funcionalidades, vou mostrar a infraestrutura. O Digi-SUS roda em 10 microsservicos independentes."

**Acoes:**
1. Mostrar o terminal com `docker-compose ps` — listar todos os containers rodando
2. Abrir o Eureka Dashboard (http://localhost:8761) — mostrar todos os servicos registrados
3. Mencionar brevemente:
   > "Temos o API Gateway na porta 8080 como ponto unico de entrada, Eureka para service discovery, PostgreSQL como banco de dados e RabbitMQ para comunicacao assincrona entre os servicos."
4. (Opcional) Abrir o RabbitMQ Management (http://localhost:15672, user: guest, password: guest) — mostrar as filas

---

### BLOCO 2 - Autenticacao e Cadastro (1 minuto)

**[Tela: Postman]**

> "Todo acesso ao sistema passa por autenticacao JWT. Vou fazer login como administrador."

**Acoes no Postman:**

1. **Login como Admin:**
   ```
   POST http://localhost:8080/autenticacao/auth/login
   Body: { "email": "admin@email.com", "senha": "admin123" }
   ```
   > "O sistema retorna um token JWT com as roles do usuario. Esse token eh validado pelo Gateway a cada requisicao."

2. **Cadastrar um novo paciente** (usando o token do admin):
   ```
   POST http://localhost:8080/autenticacao/pacientes
   Body: { nomeCompleto, email, senha, cpf, dataNascimento, telefone, endereco }
   ```
   > "Apenas administradores podem cadastrar usuarios. O sistema valida CPF, email unico, idade e todos os dados do endereco."

---

### BLOCO 3 - Agenda Medica e Auto-Agendamento (2 minutos)

**[Tela: Postman]**

> "Agora vou demonstrar o fluxo de auto-agendamento - onde o paciente escolhe o horario da consulta."

**Acoes no Postman:**

1. **Login como Medico:**
   ```
   POST http://localhost:8080/autenticacao/auth/login
   Body: { "email": "medico@email.com", "senha": "medico123" }
   ```

2. **Criar agenda do medico:**
   ```
   POST http://localhost:8080/agendamento/agenda
   Body: {
     "medicoId": 2,
     "diaSemana": "MONDAY",    <-- usar o dia da semana de HOJE para demo
     "horaInicio": "08:00",
     "horaFim": "12:00",
     "duracaoSlotMinutos": 30,
     "especialidade": "CLINICA GERAL"
   }
   ```
   > "O medico define que atende toda segunda das 8h ao meio-dia, em consultas de 30 minutos."

   **IMPORTANTE:** Usar o dia da semana correspondente ao dia da gravacao para que os slots sejam gerados para hoje.

3. **Gerar horarios disponiveis:**
   ```
   POST http://localhost:8080/agendamento/agenda/{id}/gerar-horarios
   Body: { "dataInicio": "2026-02-08", "dataFim": "2026-03-01" }
   ```
   > "O sistema gera 8 slots para cada dia que bate com o dia da semana configurado."

4. **Login como Paciente:**
   ```
   POST http://localhost:8080/autenticacao/auth/login
   Body: { "email": "paciente@email.com", "senha": "paciente123" }
   ```

5. **Buscar horarios disponiveis:**
   ```
   GET http://localhost:8080/agendamento/horarios/disponiveis
       ?especialidade=CLINICA GERAL&dataInicio=2026-02-08&dataFim=2026-02-28
   ```
   > "O paciente busca por especialidade e ve todos os horarios livres. Aqui temos os slots gerados."

6. **Auto-agendar:**
   ```
   POST http://localhost:8080/agendamento/horarios/autoagendamento
   Body: { "horarioDisponivelId": <id do primeiro slot> }
   ```
   > "O paciente escolhe o horario e a consulta eh criada automaticamente. O sistema usa lock pessimista para garantir que dois pacientes nao reservem o mesmo slot."

7. **Mostrar nos logs** (terminal com `docker-compose logs -f notificacoes`):
   > "Repare que o servico de notificacoes recebeu automaticamente o evento via RabbitMQ e simulou o envio de email."

---

### BLOCO 4 - Triagem com Classificacao de Prioridade (2 minutos)

**[Tela: Postman + terminal de logs]**

> "Agora vou demonstrar a triagem - o enfermeiro registra os sinais vitais e o sistema classifica a prioridade e cria uma consulta automaticamente."

**Acoes no Postman:**

1. **Login como Enfermeiro:**
   ```
   POST http://localhost:8080/autenticacao/auth/login
   Body: { "email": "enfermeiro@email.com", "senha": "enfermeiro123" }
   ```

2. **Triagem normal (NAO_URGENTE):**
   ```
   POST http://localhost:8080/triagem/triagem
   Body: {
     "pacienteId": 1,
     "funcionarioId": 3,
     "pressaoArterial": "120/80",
     "temperatura": 36.5,
     "batimentoCardiaco": 72,
     "conduta": "Paciente estavel, sinais vitais normais",
     "especialidade": "CLINICA GERAL"
   }
   ```
   > "Temperatura 36.5, batimento 72 - tudo normal. O sistema classificou como NAO_URGENTE."

3. **Triagem de emergencia (EMERGENCIA):**
   ```
   POST http://localhost:8080/triagem/triagem
   Body: {
     "pacienteId": 1,
     "funcionarioId": 3,
     "pressaoArterial": "180/110",
     "temperatura": 40.5,
     "batimentoCardiaco": 155,
     "conduta": "Paciente em estado critico, encaminhar imediatamente",
     "especialidade": "CLINICA GERAL"
   }
   ```
   > "Temperatura 40.5, batimento 155 - o sistema classificou como EMERGENCIA automaticamente."

4. **Mostrar nos logs do agendamento** (`docker-compose logs -f agendamento`):
   > "Vejam nos logs do agendamento: ele recebeu o evento da triagem via RabbitMQ e criou automaticamente uma consulta. Se havia slot disponivel hoje, criou como REGULAR reservando o slot. Se nao havia, criou como ENCAIXE - uma consulta extra que garante o atendimento."

5. **Verificar a consulta criada:**
   ```
   GET http://localhost:8080/agendamento/agendamento/paciente/1
   ```
   > "Aqui podemos ver a consulta criada com os campos tipoConsulta, prioridade e triagemId preenchidos."

---

### BLOCO 5 - Atendimento Medico e Exames (1.5 minutos)

**[Tela: Postman]**

> "Com a consulta agendada, o medico pode iniciar o atendimento."

**Acoes no Postman:**

1. **Login como Medico** (se necessario)

2. **Iniciar atendimento:**
   ```
   POST http://localhost:8080/atendimento/atendimentos
   Body: { "consultaId": <id da consulta agendada> }
   ```
   > "O medico inicia o atendimento vinculado a consulta. O status muda para EM_ANDAMENTO e a consulta eh marcada como REALIZADA no agendamento."

3. **Finalizar atendimento com anamnese:**
   ```
   PATCH http://localhost:8080/atendimento/atendimentos/{id}/finalizar
   Body: {
     "anamnese": "Paciente relata dores no peito ha 2 dias",
     "condutaMedica": "Solicitar ECG e retorno em 7 dias"
   }
   ```
   > "O medico registra o relato do paciente e a conduta medica. O atendimento eh finalizado."

4. **Solicitar exame:**
   ```
   POST http://localhost:8080/exames/solicitacoes
   Body: {
     "pacienteId": 1,
     "medicoId": 2,
     "tipoExameId": 1,
     "prioridade": "NORMAL",
     "observacoes": "Verificar hemoglobina"
   }
   ```
   > "O medico solicita um Hemograma. O paciente pode depois agendar o exame em um horario disponivel."

---

### BLOCO 6 - Historico Medico via GraphQL (0.5 minuto)

**[Tela: Postman ou GraphiQL]**

> "Por fim, todo esse fluxo - consultas, triagens, atendimentos - eh registrado automaticamente no historico do paciente via GraphQL."

**Acoes:**

1. **Consultar historico** (Postman ou GraphiQL em http://localhost:8080/historico/graphiql):
   ```graphql
   POST http://localhost:8080/historico/graphql
   Body: {
     "query": "{ historicoPorPaciente(pacienteId: 1) { id consultaId dataHora especialidade status observacoes } }"
   }
   ```
   > "Com uma unica query GraphQL, o medico acessa todo o historico de consultas do paciente - datas, especialidades, status e observacoes. Tudo registrado automaticamente via eventos RabbitMQ, sem nenhuma chamada manual."

---

### BLOCO 7 - Encerramento Tecnico (0.5 minuto)

**[Tela: diagrama de arquitetura ou Eureka Dashboard]**

> "Resumindo o que vimos: o Digi-SUS eh composto por 8 microsservicos independentes seguindo Clean Architecture, comunicacao sincrona via REST e assincrona via RabbitMQ, autenticacao JWT com controle de acesso por role, e banco de dados isolado por servico com migrations automaticas via Flyway."

> "Todo o codigo esta em Java 21 com Spring Boot, roda em containers Docker, e a arquitetura de microsservicos permite escalar cada servico independentemente conforme a demanda."

---

### Checklist antes de gravar

- [ ] `docker-compose up --build` rodou sem erros
- [ ] Todos os servicos aparecem UP no Eureka (http://localhost:8761)
- [ ] Collection do Postman importada (`Digi-SUS v3 - Testes Completos.postman_collection.json`)
- [ ] Executar pasta "0. Login" da collection para ter os tokens prontos
- [ ] Verificar que o dia da semana da agenda corresponde ao dia da gravacao
- [ ] Terminal aberto com `docker-compose logs -f notificacoes agendamento` para mostrar eventos
- [ ] Testar todo o fluxo uma vez antes de gravar (dry-run)
- [ ] Se necessario, resetar banco: `docker-compose down -v && docker-compose up --build`

### Ordem sugerida das chamadas no Postman

```
1.  POST /autenticacao/auth/login          (admin)
2.  POST /autenticacao/pacientes           (cadastrar paciente)
3.  POST /autenticacao/auth/login          (medico)
4.  POST /agendamento/agenda               (criar agenda)
5.  POST /agendamento/agenda/{id}/gerar-horarios
6.  POST /autenticacao/auth/login          (paciente)
7.  GET  /agendamento/horarios/disponiveis
8.  POST /agendamento/horarios/autoagendamento
9.  POST /autenticacao/auth/login          (enfermeiro)
10. POST /triagem/triagem                  (caso normal)
11. POST /triagem/triagem                  (caso emergencia)
12. GET  /agendamento/agendamento/paciente/1  (ver consultas criadas)
13. POST /autenticacao/auth/login          (medico)
14. POST /atendimento/atendimentos         (iniciar atendimento)
15. PATCH /atendimento/atendimentos/{id}/finalizar
16. POST /exames/solicitacoes              (solicitar exame)
17. POST /historico/graphql                (consultar historico)
```
