Projeto inicial Digi-SUS




### 3. Endpoints

**Estrutura Base:**

```
http://localhost:8080/{microservice}/{endpoint}
```

**Microserviço de Autenticação**

Base URL: `http://localhost:8080/autenticacao`


#### 1.1 Autenticação de Usuários

#### POST `/auth/login`

Realiza login de usuários (pacientes e funcionários).

**Acesso:** Público

**Request Body:**

```json
{
  "email": "string",
  "senha": "string"
}
```

**Response (200 OK):**

```json
{
  "token": "string",
  "type": "Bearer",
  "expiresIn": 86400000,
  "userType": "PACIENTE|MEDICO|ENFERMEIRO|ADMIN"
}
```

---

#### POST `/auth/service/token`

Obtém token de autenticação para comunicação entre microserviços.

**Acesso:** Interno (serviços)

**Request Body:**

```json
{
  "serviceId": "string",
  "serviceSecret": "string"
}
```

**Response (200 OK):**

```json
{
  "token": "string",
  "type": "Bearer",
  "expiresIn": 3600000,
  "userType": "SISTEMA"
}
```

---

#### 1.2 Gestão de Pacientes

#### POST `/pacientes`

Cadastra um novo paciente.

**Acesso:** Requer role `ADMIN`

**Request Body:**

```json
{
  "email": "string",
  "senha": "string (mínimo 6 caracteres)",
  "nomeCompleto": "string (2-100 caracteres)",
  "cpf": "string (11122233344 ou 111.222.333-44)",
  "dataNascimento": "YYYY-MM-DD",
  "telefone": "string ((11) 99999-9999)",
  "endereco": {
    "logradouro": "string (máx 200)",
    "numero": "string (máx 10)",
    "complemento": "string (máx 100) - opcional",
    "bairro": "string (máx 100)",
    "cidade": "string (máx 100)",
    "estado": "string (2 caracteres - ex: SP)",
    "cep": "string (12345-678 ou 12345678)"
  }
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "email": "string",
  "nomeCompleto": "string",
  "cpf": "string",
  "dataNascimento": "YYYY-MM-DD",
  "telefone": "string",
  "endereco": {
    "logradouro": "string",
    "numero": "string",
    "complemento": "string",
    "bairro": "string",
    "cidade": "string",
    "estado": "string",
    "cep": "string"
  },
  "ativo": true
}
```

---

#### 1.3 Gestão de Funcionários

#### POST `/funcionarios`

Cadastra um novo funcionário (médico, enfermeiro ou admin).

**Acesso:** Requer role `ADMIN`

**Request Body:**

```json
{
  "email": "string",
  "senha": "string (mínimo 6 caracteres)",
  "tipo": "ADMIN|MEDICO|ENFERMEIRO",
  "nomeCompleto": "string (2-100 caracteres)",
  "cpf": "string (11122233344 ou 111.222.333-44)",
  "crm": "string (máx 20) - obrigatório para MEDICO",
  "coren": "string (máx 20) - obrigatório para ENFERMEIRO",
  "especialidade": {
    "nome": "string (máx 100) - obrigatório para MEDICO",
    "codigo": "string (máx 10) - obrigatório para MEDICO"
  }
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "email": "string",
  "tipo": "ADMIN|MEDICO|ENFERMEIRO",
  "nomeCompleto": "string",
  "cpf": "string",
  "crm": "string",
  "coren": "string",
  "especialidade": {
    "nome": "string",
    "codigo": "string"
  },
  "ativo": true,
  "dataCadastro": "2025-01-01T10:00:00"
}
```

---

#### GET `/funcionarios/{id}`

Busca um funcionário por ID.

**Acesso:** Requer role `ADMIN`

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "string",
  "tipo": "ADMIN|MEDICO|ENFERMEIRO",
  "nomeCompleto": "string",
  "cpf": "string",
  "crm": "string",
  "coren": "string",
  "especialidade": {
    "nome": "string",
    "codigo": "string"
  },
  "ativo": true,
  "dataCadastro": "2025-01-01T10:00:00"
}
```

---

#### 1.4 Endpoints Internos

#### GET `/internal/usuarios/pacientes/{pacienteId}/exists`

Verifica se um paciente existe.

**Acesso:** Interno (serviços com role `SISTEMA`)

**Response (200 OK):**

```json
"true/false"
```

**Microserviço de Agendamento**

Base URL: `http://localhost:8080/agendamento`

#### 2.1 Gestão de Consultas

#### POST `/`

Agenda uma nova consulta.

**Acesso:** Requer role `MEDICO` ou `ENFERMEIRO`

**Request Body:**

```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "dataHora": "2025-12-31T14:00:00",
  "especialidade": "string"
}
```

**Validações:**

- `dataHora` deve ser futura
- `pacienteId` deve existir no sistema
- Todos os campos são obrigatórios

**Response (201 Created):**

```json
{
  "id": 1,
  "pacienteId": 1,
  "medicoId": 1,
  "dataHora": "2025-12-31T14:00:00",
  "especialidade": "string",
  "status": "AGENDADA"
}
```

**Headers de Response:**

```
Location: /agendamento/1
```

---

#### PUT `/{id}`

Reagenda uma consulta existente.

**Acesso:** Requer role `MEDICO` ou `ENFERMEIRO`

**Request Body:**

```json
{
  "medicoId": 1,
  "dataHora": "2025-12-31T15:00:00",
  "especialidade": "string"
}
```

**Validações:**

- `dataHora` deve ser futura
- Consulta não pode estar cancelada
- Todos os campos são obrigatórios

**Response (200 OK):**

```json
{
  "id": 1,
  "pacienteId": 1,
  "medicoId": 1,
  "dataHora": "2025-12-31T15:00:00",
  "especialidade": "string",
  "status": "AGENDADA"
}
```

---

#### DELETE `/{id}`

Cancela uma consulta.


**Acesso:** Requer role `MEDICO` ou `ENFERMEIRO`

**Validações:**

- Consulta não pode já estar cancelada
- Deve ter pelo menos 24h de antecedência

**Response (204 No Content)**

---

#### GET `/paciente/{pacienteId}`

Lista todas as consultas de um paciente.


**Acesso:** Requer role `MEDICO`, `ENFERMEIRO` ou `PACIENTE`

**Autorização:**

- Pacientes só podem visualizar suas próprias consultas
- Médicos e enfermeiros podem visualizar qualquer consulta

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "pacienteId": 1,
    "medicoId": 1,
    "dataHora": "2025-12-31T14:00:00",
    "especialidade": "CARDIOLOGIA",
    "status": "AGENDADA"
  }
]
```

**Microserviço de Historico**

Base URL: `http://localhost:8080/historico`

**Importante:** Este microserviço utiliza **GraphQL** em vez de REST.

#### 3.1 Endpoint GraphQL

#### POST `/graphql`

Endpoint único para todas as queries GraphQL.

**Acesso:** Requer role `MEDICO`, `ENFERMEIRO` ou `PACIENTE`

**Content-Type:** `application/json`

---

#### 3.2 Queries Disponíveis

#### historicoPorPaciente

Retorna o histórico de consultas de um paciente.

**Query:**

```graphql
query {
  historicoPorPaciente(pacienteId: 1) {
    id
    consultaId
    pacienteId
    medicoId
    dataHora
    especialidade
    status
    observacoes
    dataCriacao
    dataAtualizacao
  }
}
```

**Autorização:**

- Pacientes só podem visualizar seu próprio histórico
- Médicos e enfermeiros podem visualizar qualquer histórico

**Response:**

```json
{
  "data": {
    "historicoPorPaciente": [
      {
        "id": "1",
        "consultaId": "1",
        "pacienteId": "1",
        "medicoId": "1",
        "dataHora": "2025-12-31T14:00:00",
        "especialidade": "CARDIOLOGIA",
        "status": "AGENDADA",
        "observacoes": null,
        "dataCriacao": "2025-01-01T10:00:00",
        "dataAtualizacao": "2025-01-01T10:00:00"
      }
    ]
  }
}
```

---

#### historicoPorConsulta

Retorna o histórico de uma consulta específica.

**Query:**

```graphql
query {
  historicoPorConsulta(consultaId: 1) {
    id
    consultaId
    pacienteId
    medicoId
    dataHora
    especialidade
    status
    observacoes
    dataCriacao
    dataAtualizacao
  }
}
```

**Acesso:** Requer role `MEDICO` ou `ENFERMEIRO`

**Response:**

```json
{
  "data": {
    "historicoPorConsulta": {
      "id": "1",
      "consultaId": "1",
      "pacienteId": "1",
      "medicoId": "1",
      "dataHora": "2025-12-31T14:00:00",
      "especialidade": "CARDIOLOGIA",
      "status": "AGENDADA",
      "observacoes": null,
      "dataCriacao": "2025-01-01T10:00:00",
      "dataAtualizacao": "2025-01-01T10:00:00"
    }
  }
}
```

## Autenticação e Autorização

### 4 Como Usar os Endpoints Protegidos

1. **Obter Token:**

```bash
curl -X POST http://localhost:8080/autenticacao/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "medico@email.com",
    "senha": "senha123"
  }'
```

2. **Usar Token nas Requisições:**

```bash
curl -X GET http://localhost:8080/agendamento/paciente/1 \
  -H "Authorization: Bearer {seu-token-aqui}"
```


## ? Segurança

### Autenticação JWT

**Estrutura do Token:**

```json
{
  "sub": "usuario@email.com",
  "userId": 1,
  "userType": "PACIENTE",
  "roles": ["ROLE_PACIENTE"],
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Níveis de Acesso

|Perfil|Permissões|
|---|---|
|**ADMIN**|Cadastrar pacientes e funcionários, acesso total|
|**MEDICO**|Agendar, reagendar, cancelar consultas, ver histórico completo|
|**ENFERMEIRO**|Agendar, reagendar, cancelar consultas, ver histórico completo|
|**PACIENTE**|Ver apenas suas próprias consultas e histórico|

### Headers de Segurança

O Gateway injeta headers após validação do JWT:

- `X-User-ID`: ID do usuário autenticado
- `X-User-Email`: Email do usuário
- `X-User-Roles`: Roles do usuário (comma-separated)

## ? Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 (opcional, apenas para desenvolvimento local)
- Maven 3.9+ (opcional, apenas para desenvolvimento local)

### Executando com Docker

1. **Clone o repositório**

```bash
git clone https://github.com/MSFelisberto/medical-services-platform
cd medical-services-platform
```

2. **Configure as variáveis de ambiente**

```bash
# O arquivo .env já está configurado com:
AGENDAMENTO_SECRET=agendamento-super-secret-2026-dgs
HISTORICO_SECRET=historico-ultra-secret-2026-dgs
NOTIFICACOES_SECRET=notificacoes-mega-secret-2026-dgs
```

3. **Execute com Docker Compose**

```bash
docker-compose up -d
```

4. **Verifique se todos os serviços estão rodando**

```bash
docker-compose ps
```

### URLs dos Serviços

| Serviço             | URL                    | Descrição                           |
| ------------------- | ---------------------- | ----------------------------------- |
| API Gateway         | http://localhost:8080  | Ponto de entrada único              |
| Eureka Server       | http://localhost:8761  | Dashboard do Service Discovery      |
| RabbitMQ Management | http://localhost:15672 | Interface do RabbitMQ (guest/guest) |
| PostgreSQL          | localhost:5432         | Banco de dados (admin/admin)        |

### Executando Localmente (Desenvolvimento)

1. **Inicie as dependências**

```bash
# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -p 5432:5432 \
  postgres:13

# RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3.10-management
```

2. **Execute cada serviço**

```bash
# Na raiz do projeto
mvn clean install

# Eureka Server
cd server && mvn spring-boot:run

# Gateway
cd gateway && mvn spring-boot:run

# Demais serviços
cd [servico] && mvn spring-boot:run
```

## ? Como Testar

### Usando Postman

Uma collection completa está disponível no arquivo `Medical Services Platform - Coleção de Testes.postman_collection.json`.

**Importando a Collection:**

1. Abra o Postman
2. Clique em Import
3. Selecione o arquivo da collection
4. Configure a variável `base_url` para `http://localhost:8080`

### Fluxo de Teste Completo

#### 1. Autenticação

```bash
# Login como Admin
POST http://localhost:8080/autenticacao/auth/login
{
    "email": "admin@email.com",
    "senha": "senha123"
}

# Resposta
{
    "token": "eyJhbGciOiJI...",
    "type": "Bearer",
    "expiresIn": 86400000,
    "userType": "ADMIN"
}
```

#### 2. Cadastrar Paciente (Admin)

```bash
POST http://localhost:8080/autenticacao/pacientes
Authorization: Bearer {token_admin}
{
    "email": "novo.paciente@email.com",
    "senha": "senha123456",
    "nomeCompleto": "João Silva",
    "cpf": "12345678901",
    "dataNascimento": "1990-05-15",
    "telefone": "(11) 98765-4321",
    "endereco": {
        "logradouro": "Rua das Flores",
        "numero": "123",
        "complemento": "Apto 45",
        "bairro": "Centro",
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01310100"
    }
}
```

#### 3. Agendar Consulta (Médico/Enfermeiro)

```bash
POST http://localhost:8080/agendamento
Authorization: Bearer {token_medico}
{
    "pacienteId": 1,
    "medicoId": 2,
    "dataHora": "2025-12-20T14:30:00",
    "especialidade": "CARDIOLOGIA"
}
```

#### 4. Consultar Histórico via GraphQL

```bash
POST http://localhost:8080/historico/graphql
Authorization: Bearer {token_medico}
{
    "query": "query { historicoPorPaciente(pacienteId: \"1\") { id consultaId dataHora especialidade status } }"
}
```

#### 5. Listar Consultas do Paciente

```bash
GET http://localhost:8080/agendamento/paciente/1
Authorization: Bearer {token_paciente}
```

### Testando Mensageria

1. **Acesse o RabbitMQ Management**

    - URL: http://localhost:15672
    - Login: guest/guest
2. **Verifique as Filas**

    - Navegue até "Queues"
    - Observe as mensagens sendo processadas
3. **Monitore os Logs**


```bash
# Ver logs do serviço de notificações
docker logs notificacoes-ms -f

# Você verá mensagens como:
# [AGENDAR] Mensagem recebida: ConsultaDTO...
# [SendNotificationUseCaseImpl] Enviando e-mail de agendamento
```

### Testando GraphQL (Histórico)

Acesse o GraphiQL: http://localhost:8080/historico/graphiql

**Query de Exemplo:**

```graphql
query BuscarHistorico($pacienteId: ID!) {
  historicoPorPaciente(pacienteId: $pacienteId) {
    id
    consultaId
    pacienteId
    medicoId
    dataHora
    especialidade
    status
    observacoes
    dataCriacao
    dataAtualizacao
  }
}

# Variables
```
{
"pacienteId": "1"
}
```

## ? Endpoints e APIs

### API Gateway Routes

|Rota|Microserviço|Descrição|
|---|---|---|
|`/autenticacao/**`|Autenticação|Login, cadastro de usuários|
|`/agendamento/**`|Agendamento|CRUD de consultas|
|`/historico/**`|Histórico|GraphQL queries|
|`/notificacoes/**`|Notificações|Endpoints de teste|

### Principais Endpoints

#### Autenticação

- `POST /autenticacao/auth/login` - Login de usuário
- `POST /autenticacao/pacientes` - Cadastrar paciente (ADMIN)
- `POST /autenticacao/funcionarios` - Cadastrar funcionário (ADMIN)
- `GET /autenticacao/funcionarios/{id}` - Buscar funcionário (ADMIN)

#### Agendamento

- `POST /agendamento` - Agendar consulta (MEDICO/ENFERMEIRO)
- `PUT /agendamento/{id}` - Reagendar consulta (MEDICO/ENFERMEIRO)
- `DELETE /agendamento/{id}` - Cancelar consulta (MEDICO/ENFERMEIRO)
- `GET /agendamento/paciente/{id}` - Listar consultas do paciente

#### Histórico (GraphQL)

- `POST /historico/graphql` - Endpoint GraphQL

## ? Monitoramento

### Eureka Dashboard

- URL: http://localhost:8761
- Visualize todos os serviços registrados
- Monitore health status

### Logs dos Containers

```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f agendamento
```
