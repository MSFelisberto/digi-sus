# Digi-SUS - Fluxo de Negocio

Plataforma de servicos de saude do SUS (Sistema Unico de Saude) construida em arquitetura de microsservicos com Java 21, Spring Boot 3.5.5 e Spring Cloud 2025.0.0.

---

## Sumario

1. [Visao Geral da Arquitetura](#1-visao-geral-da-arquitetura)
2. [Atores e Papeis](#2-atores-e-papeis)
3. [Fluxo de Autenticacao](#3-fluxo-de-autenticacao)
4. [Cadastro de Usuarios](#4-cadastro-de-usuarios)
5. [Agendamento Tradicional de Consultas](#5-agendamento-tradicional-de-consultas)
6. [Agenda Medica e Auto-Agendamento](#6-agenda-medica-e-auto-agendamento)
7. [Agendamento de Exames](#7-agendamento-de-exames)
8. [Triagem](#8-triagem)
9. [Historico Medico](#9-historico-medico)
10. [Notificacoes](#10-notificacoes)
11. [Comunicacao Assincrona (RabbitMQ)](#11-comunicacao-assincrona-rabbitmq)
12. [Mapa Completo de Endpoints](#12-mapa-completo-de-endpoints)
13. [Regras de Negocio](#13-regras-de-negocio)
14. [Decisoes Arquiteturais](#14-decisoes-arquiteturais)

---

## 1. Visao Geral da Arquitetura

```
                          ????????????????????
                          ?   Cliente (App)   ?
                          ????????????????????
                                   ? HTTP
                                   ?
                          ????????????????????
                          ?   API Gateway    ?
                          ?   (porta 8080)   ?
                          ?                  ?
                          ?  - Valida JWT    ?
                          ?  - Injeta headers?
                          ?  - Roteia        ?
                          ????????????????????
                                  ?
                   ???????????????????????????????????
                   ?              ?                   ?
         ???????????????  ???????????????  ???????????????????
         ? Autenticacao ?  ? Agendamento ?  ?    Triagem      ?
         ?              ?  ?             ?  ?                 ?
         ? - Login      ?  ? - Consultas ?  ? - Sinais vitais ?
         ? - Cadastro   ?  ? - Agenda    ?  ? - Conduta       ?
         ? - JWT        ?  ? - Horarios  ?  ???????????????????
         ????????????????  ? - Exames    ?           ?
                           ???????????????           ?
                                  ?                  ?
                          ????????????????????????????????
                          ?         RabbitMQ              ?
                          ?  Exchange: "notificacoes"     ?
                          ????????????????????????????????
                                  ?              ?
                        ??????????????   ???????????????????
                        ? Historico   ?   ?  Notificacoes   ?
                        ?            ?   ?                 ?
                        ? GraphQL    ?   ? Email simulado  ?
                        ? Consultas  ?   ? Sem banco       ?
                        ? Triagens   ?   ???????????????????
                        ??????????????
```

### Microsservicos

| Servico | Porta | Banco | Funcao |
|---------|-------|-------|--------|
| **server** | 8761 | - | Eureka Service Registry - descoberta de servicos |
| **gateway** | 8080 | - | API Gateway - roteamento, validacao JWT, injecao de headers |
| **autenticacao** | dinamica | db_autenticacao | Cadastro de pacientes/funcionarios, emissao e validacao JWT |
| **agendamento** | dinamica | db_agendamento | Consultas, agendas medicas, horarios, exames |
| **historico** | dinamica | db_historico | Historico medico via GraphQL, consome eventos RabbitMQ |
| **notificacoes** | dinamica | - | Consome eventos e simula envio de emails (stateless) |
| **triagem** | dinamica | db_triagem | Registro de sinais vitais e conduta de enfermagem |
| **commons** | - | - | Biblioteca compartilhada (DTOs, configuracao RabbitMQ) |

Todos os servicos (exceto server e gateway) usam portas dinamicas (`server.port=0`) e se registram no Eureka. O gateway descobre os servicos via Eureka usando load balancing (`lb://nome-servico`).

---

## 2. Atores e Papeis

| Ator | Role JWT | Descricao |
|------|----------|-----------|
| **Paciente** | `ROLE_PACIENTE` | Cidadao que busca atendimento no SUS. Pode auto-agendar consultas e exames, visualizar seu proprio historico |
| **Medico** | `ROLE_MEDICO` | Profissional medico. Agenda/reagenda/cancela consultas, solicita exames, define agenda de atendimento. Requer CRM e Especialidade |
| **Enfermeiro** | `ROLE_ENFERMEIRO` | Profissional de enfermagem. Agenda/reagenda/cancela consultas, realiza triagem. Requer COREN |
| **Administrador** | `ROLE_ADMIN` | Gestor do sistema. Cadastra usuarios, cria tipos de exame, gerencia agendas de exames |
| **Atendente** | `ROLE_ATENDENTE` | Recepcionista. Auxilia pacientes no agendamento de consultas e exames |
| **Tecnico de Laboratorio** | `ROLE_TECNICO_LABORATORIO` | Profissional de laboratorio. Tipo cadastrado mas sem endpoints especificos ainda |
| **Sistema** | `ROLE_SISTEMA` | Comunicacao entre microsservicos (service-to-service). Usado internamente |

### Validacoes por Tipo de Funcionario

| Tipo | CRM | COREN | Especialidade |
|------|-----|-------|---------------|
| MEDICO | Obrigatorio | Proibido | Obrigatoria |
| ENFERMEIRO | Proibido | Obrigatorio | Proibida |
| ADMIN | Proibido | Proibido | Proibida |
| TECNICO_LABORATORIO | Proibido | Proibido | Proibida |
| ATENDENTE | Proibido | Proibido | Proibida |

---

## 3. Fluxo de Autenticacao

### 3.1 Login de Usuario

```
Cliente                    Gateway                  Autenticacao
  ?                          ?                          ?
  ?  POST /autenticacao/     ?                          ?
  ?       auth/login         ?                          ?
  ?  { email, senha }        ?                          ?
  ??????????????????????????>?                          ?
  ?                          ?  (rota aberta, sem JWT)  ?
  ?                          ?  POST /auth/login        ?
  ?                          ??????????????????????????>?
  ?                          ?                          ? Busca Paciente ou
  ?                          ?                          ? Funcionario por email
  ?                          ?                          ? Valida senha (BCrypt)
  ?                          ?                          ? Gera JWT com:
  ?                          ?                          ?  - userId
  ?                          ?                          ?  - email
  ?                          ?                          ?  - roles[]
  ?                          ?  { token, type, expires, ?
  ?                          ?    userType }            ?
  ?                          ?<??????????????????????????
  ?  { token, "Bearer",     ?                          ?
  ?    86400000, "MEDICO" }  ?                          ?
  ?<??????????????????????????                          ?
```

**Response de login:**
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "userType": "MEDICO"
}
```

### 3.2 Requisicao Autenticada (fluxo do Gateway)

```
Cliente                    Gateway                  Microsservico
  ?                          ?                          ?
  ?  GET /agendamento/...    ?                          ?
  ?  Authorization: Bearer X ?                          ?
  ??????????????????????????>?                          ?
  ?                          ? 1. RouterValidator:      ?
  ?                          ?    rota eh protegida?    ?
  ?                          ?    ("/auth/login" e      ?
  ?                          ?    "/internal/" sao      ?
  ?                          ?    abertas, resto nao)   ?
  ?                          ?                          ?
  ?                          ? 2. Extrai token do       ?
  ?                          ?    header Authorization  ?
  ?                          ?                          ?
  ?                          ? 3. Valida JWT:           ?
  ?                          ?    - Assinatura          ?
  ?                          ?    - Expiracao           ?
  ?                          ?                          ?
  ?                          ? 4. Extrai claims:        ?
  ?                          ?    - userId, email,      ?
  ?                          ?      roles               ?
  ?                          ?                          ?
  ?                          ? 5. Injeta headers:       ?
  ?                          ?    X-User-ID: 1          ?
  ?                          ?    X-User-Email: a@b.com ?
  ?                          ?    X-User-Roles: ROLE_X  ?
  ?                          ?                          ?
  ?                          ?  Requisicao + headers    ?
  ?                          ??????????????????????????>?
  ?                          ?                          ? SecurityFilter le
  ?                          ?                          ? headers X-User-*
  ?                          ?                          ? Verifica @PreAuthorize
  ?                          ?  Response               ?
  ?                          ?<??????????????????????????
  ?  Response                ?                          ?
  ?<??????????????????????????                          ?
```

### 3.3 Autenticacao entre Servicos (Service-to-Service)

O microsservico agendamento precisa verificar se um paciente existe chamando o autenticacao. Para isso:

1. Agendamento envia `POST /autenticacao/auth/service/token` com `serviceId` e `serviceSecret`
2. Autenticacao valida as credenciais e retorna JWT com `ROLE_SISTEMA`
3. Agendamento usa esse token para chamar `GET /autenticacao/internal/usuarios/pacientes/{id}/exists`
4. Autenticacao retorna `true/false`

---

## 4. Cadastro de Usuarios

### 4.1 Cadastrar Paciente

**Endpoint:** `POST /autenticacao/pacientes` (requer `ROLE_ADMIN`)

```json
{
  "nomeCompleto": "Joao da Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "cpf": "12345678901",
  "dataNascimento": "1990-05-15",
  "telefone": "11999998888",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 4",
    "bairro": "Centro",
    "cidade": "Sao Paulo",
    "estado": "SP",
    "cep": "01001000"
  }
}
```

**Validacoes de dominio:**
- Email: formato valido, unico no sistema
- Senha: minimo 6 caracteres (armazenada com BCrypt)
- CPF: exatamente 11 digitos
- Idade: entre 0 e 120 anos (calculada a partir da data de nascimento)
- CEP: exatamente 8 digitos
- Todos os campos do endereco obrigatorios (exceto complemento)

### 4.2 Cadastrar Funcionario

**Endpoint:** `POST /autenticacao/funcionarios` (requer `ROLE_ADMIN`)

```json
{
  "nomeCompleto": "Dra. Maria Santos",
  "email": "maria@hospital.com",
  "senha": "senha123",
  "cpf": "98765432101",
  "tipo": "MEDICO",
  "crm": "123456",
  "especialidade": {
    "nome": "Cardiologia",
    "codigo": "CARDIO"
  }
}
```

O tipo determina quais campos sao obrigatorios/proibidos (ver tabela na secao 2).

---

## 5. Agendamento Tradicional de Consultas

Fluxo onde o profissional de saude (medico/enfermeiro) agenda diretamente uma consulta para o paciente.

### 5.1 Agendar Consulta

**Endpoint:** `POST /agendamento/agendamento` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

```
Medico/Enfermeiro             Agendamento              RabbitMQ
  ?                              ?                        ?
  ? POST /agendamento            ?                        ?
  ? { pacienteId, medicoId,      ?                        ?
  ?   dataHora, especialidade }  ?                        ?
  ??????????????????????????????>?                        ?
  ?                              ? 1. Valida paciente     ?
  ?                              ?    existe (chama       ?
  ?                              ?    autenticacao)       ?
  ?                              ? 2. Valida medico       ?
  ?                              ?    existe e esta ativo ?
  ?                              ? 3. Valida dataHora     ?
  ?                              ?    eh futura           ?
  ?                              ? 4. Cria Consulta       ?
  ?                              ?    status=AGENDADA     ?
  ?                              ? 5. Salva no banco      ?
  ?                              ?                        ?
  ?                              ? Publica 2 eventos:     ?
  ?                              ?????????????????????????>?
  ?                              ? a) ConsultaDTO         ?
  ?                              ?    routing: notificacao ?
  ?                              ?    .agendar            ?
  ?                              ? b) HistoricoEventDTO   ?
  ?                              ?    tipo: AGENDADA      ?
  ?                              ?    routing: notificacao ?
  ?                              ?    .historico          ?
  ?  { id, pacienteId,          ?                        ?
  ?    medicoId, dataHora,       ?                        ?
  ?    especialidade,            ?                        ?
  ?    status: "AGENDADA" }      ?                        ?
  ?<??????????????????????????????                        ?
```

### 5.2 Reagendar Consulta

**Endpoint:** `PUT /agendamento/agendamento/{id}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

```json
{
  "medicoId": 2,
  "dataHora": "2026-03-20T10:00:00",
  "especialidade": "ORTOPEDIA"
}
```

**Regras:**
- Nao permite reagendar consulta com status `CANCELADA`
- Nova data deve ser futura
- Permite trocar medico e especialidade
- Publica eventos: `notificacao.reagendar` + `notificacao.historico` (tipo `REAGENDADA`)

### 5.3 Cancelar Consulta

**Endpoint:** `DELETE /agendamento/agendamento/{id}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

**Regras:**
- Nao permite cancelar consulta ja cancelada
- **Minimo 24 horas de antecedencia** (regra de negocio critica)
- Se a consulta foi criada via auto-agendamento (possui HorarioDisponivel vinculado), o slot eh liberado automaticamente
- Publica eventos: `notificacao.cancelar` + `notificacao.historico` (tipo `CANCELADA`)

### 5.4 Listar Consultas do Paciente

**Endpoint:** `GET /agendamento/agendamento/paciente/{pacienteId}` (requer `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_PACIENTE`)

**Regra de acesso:** Paciente so consegue visualizar suas proprias consultas (o sistema compara o userId do token com o pacienteId). Medico e Enfermeiro podem ver de qualquer paciente.

---

## 6. Agenda Medica e Auto-Agendamento

Feature principal de inovacao: medicos definem horarios de atendimento e pacientes podem se auto-agendar nos slots disponiveis.

### 6.1 Criar Agenda do Medico

**Endpoint:** `POST /agendamento/agenda` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

```json
{
  "medicoId": 1,
  "diaSemana": "MONDAY",
  "horaInicio": "08:00",
  "horaFim": "12:00",
  "duracaoSlotMinutos": 30,
  "especialidade": "CARDIOLOGIA"
}
```

Isso define que o medico atende toda segunda-feira das 8h as 12h em slots de 30 minutos na especialidade Cardiologia.

**Validacoes:**
- `horaFim` deve ser posterior a `horaInicio`
- `duracaoSlotMinutos` deve ser positivo e caber no intervalo (4 horas / 30 min = 8 slots)
- Medico deve existir e estar ativo

### 6.2 Gerar Horarios Disponiveis

**Endpoint:** `POST /agendamento/agenda/{id}/gerar-horarios` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

```json
{
  "dataInicio": "2026-02-03",
  "dataFim": "2026-03-01"
}
```

**Logica:**
```
Para cada data no periodo [dataInicio, dataFim]:
  Se data.dayOfWeek == agenda.diaSemana:
    Para cada slot de horaInicio ate horaFim (incremento = duracaoSlotMinutos):
      Criar HorarioDisponivel:
        - medicoId = agenda.medicoId
        - dataHora = data + horaSlot
        - especialidade = agenda.especialidade
        - ocupado = false
        - consultaId = null
```

Exemplo: para uma agenda de segunda das 8h-12h com slots de 30min, gerar horarios entre 03/02 e 01/03 cria:
- 03/02: 08:00, 08:30, 09:00, 09:30, 10:00, 10:30, 11:00, 11:30 (8 slots)
- 10/02: 08:00, 08:30, ... (mais 8 slots)
- 17/02: ... e assim por diante

Os slots sao **persistidos no banco** (`tb_horarios_disponiveis`) para permitir controle de concorrencia com `SELECT FOR UPDATE`.

### 6.3 Buscar Horarios Disponiveis (Paciente)

**Endpoint:** `GET /agendamento/horarios/disponiveis?especialidade=CARDIOLOGIA&dataInicio=2026-02-03&dataFim=2026-02-28` (requer autenticacao)

Retorna todos os slots com `ocupado=false` para a especialidade e periodo informados:

```json
[
  {
    "id": 1,
    "medicoId": 1,
    "dataHora": "2026-02-03T08:00:00",
    "especialidade": "CARDIOLOGIA"
  },
  {
    "id": 2,
    "medicoId": 1,
    "dataHora": "2026-02-03T08:30:00",
    "especialidade": "CARDIOLOGIA"
  }
]
```

### 6.4 Auto-Agendamento (Paciente)

**Endpoint:** `POST /agendamento/horarios/autoagendamento` (requer `ROLE_PACIENTE` ou `ROLE_ATENDENTE`)

```json
{
  "horarioDisponivelId": 1
}
```

```
Paciente                    Agendamento                    Banco
  ?                              ?                           ?
  ? POST /horarios/              ?                           ?
  ?   autoagendamento            ?                           ?
  ? { horarioDisponivelId: 1 }   ?                           ?
  ??????????????????????????????>?                           ?
  ?                              ? 1. SELECT FOR UPDATE      ?
  ?                              ?    tb_horarios_disponiveis?
  ?                              ?    WHERE id = 1           ?
  ?                              ????????????????????????????>?
  ?                              ?    (lock pessimista)      ?
  ?                              ?<???????????????????????????
  ?                              ?                           ?
  ?                              ? 2. Verifica ocupado=false ?
  ?                              ?                           ?
  ?                              ? 3. Valida paciente existe ?
  ?                              ?    (chama autenticacao)   ?
  ?                              ?                           ?
  ?                              ? 4. Cria Consulta:         ?
  ?                              ?    pacienteId = userId    ?
  ?                              ?      do token             ?
  ?                              ?    medicoId = do slot     ?
  ?                              ?    especialidade = do slot?
  ?                              ?    dataHora = do slot     ?
  ?                              ?    status = AGENDADA      ?
  ?                              ?                           ?
  ?                              ? 5. Marca slot:            ?
  ?                              ?    ocupado = true         ?
  ?                              ?    consultaId = nova ID   ?
  ?                              ?                           ?
  ?                              ? 6. Publica eventos        ?
  ?                              ?    RabbitMQ               ?
  ?                              ?                           ?
  ?  { id, pacienteId,          ?                           ?
  ?    medicoId, dataHora,       ?                           ?
  ?    especialidade,            ?                           ?
  ?    status: "AGENDADA" }      ?                           ?
  ?<??????????????????????????????                           ?
```

**Prevencao de double-booking:** O `SELECT FOR UPDATE` (lock pessimista) garante que dois pacientes nao conseguem reservar o mesmo slot simultaneamente. Se o slot ja estiver ocupado, o sistema retorna erro `HorarioIndisponivelException`.

### 6.5 Liberacao de Slot ao Cancelar

Quando uma consulta criada via auto-agendamento eh cancelada (secao 5.3), o sistema:
1. Busca o `HorarioDisponivel` vinculado via `consultaId`
2. Chama `horario.liberar()` que marca `ocupado=false` e limpa `consultaId`
3. O slot volta a aparecer na busca de horarios disponiveis

### 6.6 Desativar Agenda

**Endpoint:** `DELETE /agendamento/agenda/{id}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

Marca a agenda como `ativa=false`. Horarios ja gerados permanecem no banco mas novas geracoes nao sao possiveis.

### 6.7 Listar Agendas do Medico

**Endpoint:** `GET /agendamento/agenda/medico/{medicoId}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

---

## 7. Agendamento de Exames

Fluxo completo de exames: medico solicita, admin configura agendas, paciente agenda.

### 7.1 Cadastrar Tipo de Exame

**Endpoint:** `POST /agendamento/exames/tipos` (requer `ROLE_ADMIN`)

```json
{
  "nome": "Hemograma Completo",
  "codigo": "HEMO",
  "descricao": "Exame de sangue completo",
  "preparacao": "Jejum de 8 horas"
}
```

**Tipos pre-cadastrados via migration:** Hemograma, Raio-X, Ultrassom, ECG, Glicemia.

**Consultar tipos:** `GET /agendamento/exames/tipos` (autenticado) e `GET /agendamento/exames/tipos/{id}` (autenticado)

### 7.2 Medico Solicita Exame

**Endpoint:** `POST /agendamento/exames/solicitacoes` (requer `ROLE_MEDICO`)

```json
{
  "pacienteId": 1,
  "medicoId": 2,
  "tipoExameId": 1,
  "prioridade": "NORMAL",
  "observacoes": "Verificar niveis de hemoglobina"
}
```

```
Medico                      Agendamento                  RabbitMQ
  ?                              ?                          ?
  ? POST /exames/solicitacoes    ?                          ?
  ??????????????????????????????>?                          ?
  ?                              ? 1. Valida paciente       ?
  ?                              ? 2. Valida tipo exame     ?
  ?                              ? 3. Cria SolicitacaoExame ?
  ?                              ?    status = PENDENTE     ?
  ?                              ?    prioridade = NORMAL   ?
  ?                              ? 4. Salva                 ?
  ?                              ?                          ?
  ?                              ? Publica ExameEventDTO    ?
  ?                              ? tipo: SOLICITADA         ?
  ?                              ? routing: notificacao     ?
  ?                              ?   .exame.solicitar       ?
  ?                              ??????????????????????????>?
  ?                              ?                          ?
  ?  { id, pacienteId, medico,  ?                          ?
  ?    tipoExame, prioridade,    ?                          ?
  ?    status: "PENDENTE",       ?                          ?
  ?    dataCriacao }             ?                          ?
  ?<??????????????????????????????                          ?
```

**Prioridades:** `NORMAL`, `URGENTE`

**Status da solicitacao:** `PENDENTE` -> `AGENDADA` -> `REALIZADA` ou `CANCELADA`

### 7.3 Admin Cria Agenda de Exame

**Endpoint:** `POST /agendamento/exames/agendamentos/agenda` (requer `ROLE_ADMIN`)

```json
{
  "tipoExameId": 1,
  "diaSemana": "TUESDAY",
  "horaInicio": "07:00",
  "horaFim": "12:00",
  "duracaoSlotMinutos": 20,
  "vagasPorSlot": 3
}
```

Diferente da agenda medica, a agenda de exame tem **multiplas vagas por slot** (`vagasPorSlot`). Isso significa que ate 3 pacientes podem agendar no mesmo horario (ex: 3 coletas de sangue simultaneas).

### 7.4 Paciente Busca Vagas de Exame

**Endpoint:** `GET /agendamento/exames/agendamentos/vagas?tipoExameId=1&dataInicio=2026-02-03&dataFim=2026-02-28` (autenticado)

**Logica (vagas computadas dinamicamente):**
```
Para cada data no periodo:
  Se data.dayOfWeek bate com alguma AgendaExame ativa do tipoExameId:
    Para cada slot gerado pela agenda:
      agendamentosExistentes = COUNT de AgendamentoExame
        WHERE dataHora = slot AND tipoExameId = X AND status != CANCELADO
      vagasRestantes = vagasPorSlot - agendamentosExistentes
      Se vagasRestantes > 0:
        Incluir no resultado
```

**Response:**
```json
[
  {
    "dataHora": "2026-02-04T07:00:00",
    "vagasRestantes": 3,
    "tipoExameId": 1
  },
  {
    "dataHora": "2026-02-04T07:20:00",
    "vagasRestantes": 2,
    "tipoExameId": 1
  }
]
```

**Diferenca arquitetural vs consultas:** As vagas de exame sao **computadas** em tempo real (nao persistidas como os slots de consulta). Isso funciona bem porque multiplas vagas por slot reduzem a contencao - a chance de dois pacientes disputarem a ultima vaga eh menor.

### 7.5 Paciente Agenda Exame

**Endpoint:** `POST /agendamento/exames/agendamentos` (requer `ROLE_PACIENTE`, `ROLE_ATENDENTE` ou `ROLE_ADMIN`)

```json
{
  "solicitacaoExameId": 1,
  "dataHora": "2026-02-04T07:00:00"
}
```

```
Paciente                    Agendamento                  RabbitMQ
  ?                              ?                          ?
  ? POST /exames/agendamentos    ?                          ?
  ??????????????????????????????>?                          ?
  ?                              ? 1. Busca SolicitacaoExame?
  ?                              ? 2. Valida status ==      ?
  ?                              ?    PENDENTE              ?
  ?                              ? 3. Valida dataHora futura?
  ?                              ? 4. Valida vagas          ?
  ?                              ?    disponiveis no slot   ?
  ?                              ? 5. Cria AgendamentoExame ?
  ?                              ?    status = AGENDADO     ?
  ?                              ? 6. Atualiza solicitacao: ?
  ?                              ?    status = AGENDADA     ?
  ?                              ? 7. Salva ambos           ?
  ?                              ?                          ?
  ?                              ? Publica ExameEventDTO    ?
  ?                              ? tipo: AGENDADA           ?
  ?                              ? routing: notificacao     ?
  ?                              ?   .exame.agendar         ?
  ?                              ??????????????????????????>?
  ?                              ?                          ?
  ?  { id, solicitacaoExameId,  ?                          ?
  ?    dataHora,                 ?                          ?
  ?    status: "AGENDADO",       ?                          ?
  ?    dataCriacao }             ?                          ?
  ?<??????????????????????????????                          ?
```

### 7.6 Cancelar Agendamento de Exame

**Endpoint:** `DELETE /agendamento/exames/agendamentos/{id}` (requer `ROLE_PACIENTE` ou `ROLE_ADMIN`)

- Marca AgendamentoExame como `CANCELADO`
- Retorna SolicitacaoExame para status `PENDENTE` (permite re-agendamento)
- Publica evento `notificacao.exame.cancelar`
- A vaga eh automaticamente liberada (modelo computado)

### 7.7 Listar Solicitacoes do Paciente

**Endpoint:** `GET /agendamento/exames/solicitacoes/paciente/{pacienteId}` (requer `ROLE_MEDICO`, `ROLE_PACIENTE` ou `ROLE_ADMIN`)

Mesma regra de acesso das consultas: paciente so ve as proprias solicitacoes.

### 7.8 Cancelar Solicitacao de Exame

**Endpoint:** `DELETE /agendamento/exames/solicitacoes/{id}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

---

## 8. Triagem

Registro de sinais vitais e avaliacao de enfermagem ao receber o paciente.

**Endpoint:** `POST /triagem/triagem` (requer autenticacao)

```json
{
  "pacienteId": 1,
  "funcionarioId": 3,
  "pressaoArterial": "120/80",
  "temperatura": 36.5,
  "batimentoCardiaco": 72,
  "conduta": "Paciente estavel, encaminhar para consulta"
}
```

**Validacoes clinicas:**
- Temperatura: entre 30.0 C e 45.0 C
- Batimento cardiaco: entre 30 e 250 bpm
- Todos os campos obrigatorios

**Eventos publicados:**
1. `TriagemAtendimentoDTO` na routing key `triagem.atendimento` - para uso imediato
2. `TriagemHistoricoDTO` na routing key `triagem.historico` - para persistencia no historico

---

## 9. Historico Medico

O servico de historico consome eventos RabbitMQ e disponibiliza dados via **GraphQL** (nao REST).

### 9.1 Schema GraphQL

```graphql
type Query {
  historicoPorPaciente(pacienteId: ID!): [HistoricoConsulta!]!
  historicoPorConsulta(consultaId: ID!): HistoricoConsulta
}

type HistoricoConsulta {
  id: ID!
  consultaId: ID!
  pacienteId: ID!
  medicoId: ID!
  dataHora: String!
  especialidade: String!
  status: String!
  observacoes: String
  dataCriacao: String!
  dataAtualizacao: String!
}
```

**Acesso:** via `POST /historico/graphql` (requer `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_PACIENTE`)

**Interface grafica:** GraphiQL disponivel em `/historico/graphiql`

### 9.2 Como os Dados Chegam ao Historico

O historico **nunca eh chamado diretamente** pelo agendamento. Os dados chegam exclusivamente via RabbitMQ:

```
Agendamento ??publish??> RabbitMQ ??consume??> Historico
```

| Evento | Acao no Historico |
|--------|-------------------|
| Consulta AGENDADA | Cria `HistoricoConsulta` com status `AGENDADA` |
| Consulta CANCELADA | Atualiza status para `CANCELADA` e adiciona motivo |
| Consulta REAGENDADA | Atualiza dataHora, medico, especialidade |
| Triagem realizada | Persiste `TriagemHistoricoEntity` com sinais vitais |

---

## 10. Notificacoes

Servico **stateless** (sem banco de dados) que consome eventos do RabbitMQ e simula o envio de emails/notificacoes.

### Eventos Consumidos

| Fila | Acao |
|------|------|
| `notificacao.agendar.queue` | Log: "Notificacao de agendamento para paciente X" |
| `notificacao.cancelar.queue` | Log: "Notificacao de cancelamento para paciente X" |
| `notificacao.reagendar.queue` | Log: "Notificacao de reagendamento para paciente X" |
| `notificacao.exame.queue` | Log: "Notificacao de solicitacao de exame para paciente X" |
| `notificacao.exame.agendar` | Log: "Notificacao de exame agendado para paciente X" |
| `notificacao.exame.cancelar` | Log: "Notificacao de exame cancelado para paciente X" |

Em producao, esses logs seriam substituidos por chamadas reais a servicos de email/SMS.

---

## 11. Comunicacao Assincrona (RabbitMQ)

### Topologia

```
Exchange: "notificacoes" (Topic Exchange)
?
??? Routing: notificacao.agendar     ??> Queue: notificacao.agendar.queue     ??> Notificacoes
??? Routing: notificacao.cancelar    ??> Queue: notificacao.cancelar.queue    ??> Notificacoes
??? Routing: notificacao.reagendar   ??> Queue: notificacao.reagendar.queue   ??> Notificacoes
??? Routing: notificacao.historico   ??> Queue: notificacao.historico.queue   ??> Historico
?
??? Routing: notificacao.exame.solicitar ??> Queue: notificacao.exame.queue   ??> Notificacoes
??? Routing: notificacao.exame.agendar   ??> Queue: notificacao.exame.agendar ??> Notificacoes
??? Routing: notificacao.exame.cancelar  ??> Queue: notificacao.exame.cancelar??> Notificacoes
?
??? Routing: triagem.atendimento    ??> Queue: triagem.atendimento.queue     ??> (consumidor futuro)
??? Routing: triagem.historico      ??> Queue: triagem.historico.queue       ??> Historico
```

### DTOs de Mensagem

**ConsultaDTO** (consultas tradicionais e auto-agendamento):
- pacienteId, medicoId, dataHora, especialidade

**HistoricoEventDTO** (historico de consultas):
- consultaId, pacienteId, medicoId, dataHora, especialidade, tipoEvento (AGENDADA/CANCELADA/REAGENDADA)

**ExameEventDTO** (exames):
- solicitacaoExameId, pacienteId, medicoId, tipoExameNome, prioridade, dataHora, tipoEvento (SOLICITADA/AGENDADA/CANCELADA)

**TriagemAtendimentoDTO** (atendimento de triagem):
- pacienteId, triagemId, dadosClinicos, conduta

**TriagemHistoricoDTO** (historico de triagem):
- triagemId, pacienteId, funcionarioId, pressaoArterial, temperatura, batimentoCardiaco, conduta

---

## 12. Mapa Completo de Endpoints

Todos os endpoints sao acessados via gateway na porta **8080** com o prefixo do servico.

### Autenticacao (`/autenticacao/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/autenticacao/auth/login` | Publico | Login (retorna JWT) |
| POST | `/autenticacao/auth/service/token` | Publico | Login service-to-service |
| POST | `/autenticacao/pacientes` | ADMIN | Cadastrar paciente |
| POST | `/autenticacao/funcionarios` | ADMIN | Cadastrar funcionario |
| GET | `/autenticacao/funcionarios/{id}` | ADMIN | Buscar funcionario por ID |
| GET | `/autenticacao/internal/usuarios/pacientes/{id}/exists` | SISTEMA | Verificar se paciente existe |

### Agendamento - Consultas (`/agendamento/agendamento/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/agendamento` | MEDICO, ENFERMEIRO | Agendar consulta |
| PUT | `/agendamento/agendamento/{id}` | MEDICO, ENFERMEIRO | Reagendar consulta |
| DELETE | `/agendamento/agendamento/{id}` | MEDICO, ENFERMEIRO | Cancelar consulta |
| GET | `/agendamento/agendamento/paciente/{pacienteId}` | MEDICO, ENFERMEIRO, PACIENTE | Listar consultas do paciente |

### Agendamento - Agenda Medica (`/agendamento/agenda/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/agenda` | MEDICO, ADMIN | Criar agenda |
| DELETE | `/agendamento/agenda/{id}` | MEDICO, ADMIN | Desativar agenda |
| GET | `/agendamento/agenda/medico/{medicoId}` | MEDICO, ADMIN | Listar agendas do medico |
| POST | `/agendamento/agenda/{id}/gerar-horarios` | MEDICO, ADMIN | Gerar horarios disponiveis |

### Agendamento - Horarios (`/agendamento/horarios/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| GET | `/agendamento/horarios/disponiveis` | Autenticado | Buscar horarios disponiveis (params: especialidade, dataInicio, dataFim) |
| POST | `/agendamento/horarios/autoagendamento` | PACIENTE, ATENDENTE | Auto-agendar consulta em slot disponivel |

### Agendamento - Tipos de Exame (`/agendamento/exames/tipos/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/exames/tipos` | ADMIN | Criar tipo de exame |
| GET | `/agendamento/exames/tipos` | Autenticado | Listar todos os tipos |
| GET | `/agendamento/exames/tipos/{id}` | Autenticado | Buscar tipo por ID |

### Agendamento - Solicitacoes de Exame (`/agendamento/exames/solicitacoes/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/exames/solicitacoes` | MEDICO | Solicitar exame |
| GET | `/agendamento/exames/solicitacoes/paciente/{pacienteId}` | MEDICO, PACIENTE, ADMIN | Listar solicitacoes do paciente |
| DELETE | `/agendamento/exames/solicitacoes/{id}` | MEDICO, ADMIN | Cancelar solicitacao |

### Agendamento - Agendamentos de Exame (`/agendamento/exames/agendamentos/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/exames/agendamentos/agenda` | ADMIN | Criar agenda de exame |
| GET | `/agendamento/exames/agendamentos/vagas` | Autenticado | Buscar vagas (params: tipoExameId, dataInicio, dataFim) |
| POST | `/agendamento/exames/agendamentos` | PACIENTE, ATENDENTE, ADMIN | Agendar exame |
| DELETE | `/agendamento/exames/agendamentos/{id}` | PACIENTE, ADMIN | Cancelar agendamento de exame |

### Historico (`/historico/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/historico/graphql` | MEDICO, ENFERMEIRO, PACIENTE | Consultar historico via GraphQL |
| GET | `/historico/graphiql` | - | Interface grafica GraphiQL |

### Triagem (`/triagem/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/triagem/triagem` | Autenticado | Realizar triagem |

---

## 13. Regras de Negocio

### Consultas

| Regra | Descricao |
|-------|-----------|
| Data futura | Toda consulta deve ter data/hora no futuro |
| Cancelamento 24h | Nao permite cancelar com menos de 24 horas de antecedencia |
| Consulta cancelada | Nao permite reagendar consulta ja cancelada |
| Acesso paciente | Paciente so ve suas proprias consultas |
| Slot unico | Um slot so pode ser reservado por um paciente (lock pessimista) |
| Liberacao automatica | Cancelar consulta libera o slot vinculado |

### Exames

| Regra | Descricao |
|-------|-----------|
| Solicitacao medica | Apenas medico pode solicitar exame |
| Agendamento sobre pendente | So permite agendar exame com solicitacao PENDENTE |
| Vagas por slot | Multiplos pacientes podem agendar no mesmo horario (ate `vagasPorSlot`) |
| Cancelamento retorna pendente | Cancelar agendamento retorna solicitacao para PENDENTE |
| Data futura | Agendamento de exame deve ser em data futura |

### Usuarios

| Regra | Descricao |
|-------|-----------|
| Email unico | Nao permite dois usuarios com mesmo email |
| CPF 11 digitos | CPF deve ter exatamente 11 digitos |
| Senha minima | Minimo 6 caracteres |
| Idade valida | Entre 0 e 120 anos |
| CEP 8 digitos | CEP deve ter exatamente 8 digitos |
| Tipo determina campos | CRM/COREN/Especialidade validados conforme tipo de funcionario |

### Triagem

| Regra | Descricao |
|-------|-----------|
| Temperatura | Entre 30.0 C e 45.0 C |
| Batimento cardiaco | Entre 30 e 250 bpm |
| Todos obrigatorios | Pressao, temperatura, batimento e conduta sao obrigatorios |

---

## 14. Decisoes Arquiteturais

### Clean Architecture

Cada microsservico segue rigorosamente:

```
domain/         Entidades, value objects, excecoes - Java puro, ZERO imports de framework
     ?
application/    Use cases, ports, DTOs - depende apenas do domain
     ?
infrastructure/ Controllers, JPA, security, config - depende de domain + application
```

- Entidades de dominio sao separadas das JPA entities
- Mapping domain <-> JPA entity acontece exclusivamente nos adapters de infraestrutura
- Beans wired via `BeansConfiguration` (nao via `@Service` nas classes de application)

### Slots Persistidos vs Vagas Computadas

| Aspecto | Consultas (slots) | Exames (vagas) |
|---------|-------------------|----------------|
| Armazenamento | Persistidos em `tb_horarios_disponiveis` | Computados em tempo real |
| Concorrencia | `SELECT FOR UPDATE` (lock pessimista) | N vagas por slot, menor contencao |
| Razao | 1 vaga por slot = alta contencao, precisa lock | N vagas por slot = contencao diluida |

### Comunicacao

| Tipo | Uso |
|------|-----|
| Sincrona (REST) | Gateway -> servicos; Agendamento -> Autenticacao (validar paciente) |
| Assincrona (RabbitMQ) | Agendamento -> Historico; Agendamento -> Notificacoes; Triagem -> Historico |

A comunicacao assincrona garante que o agendamento nao fica bloqueado esperando o historico ou notificacoes processarem. Se um desses servicos estiver fora do ar, as mensagens ficam na fila e sao processadas quando o servico voltar.
