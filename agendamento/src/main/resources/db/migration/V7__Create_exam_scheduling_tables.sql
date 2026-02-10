-- Recria tabelas de agendamento de exames no MS-Agendamento (com slots materializados e suporte a pessimistic locking)

CREATE TABLE tb_agendas_exame (
    id BIGSERIAL PRIMARY KEY,
    tipo_exame_id BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    duracao_slot_minutos INTEGER NOT NULL,
    vagas_por_slot INTEGER NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT chk_exame_dia_semana CHECK (dia_semana IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    CONSTRAINT chk_exame_horario CHECK (hora_fim > hora_inicio),
    CONSTRAINT chk_exame_duracao CHECK (duracao_slot_minutos > 0),
    CONSTRAINT chk_exame_vagas CHECK (vagas_por_slot > 0)
);

CREATE TABLE tb_horarios_exame_disponiveis (
    id BIGSERIAL PRIMARY KEY,
    agenda_exame_id BIGINT NOT NULL REFERENCES tb_agendas_exame(id),
    tipo_exame_id BIGINT NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    vagas_totais INTEGER NOT NULL,
    vagas_ocupadas INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_horario_exame_unico UNIQUE (tipo_exame_id, data_hora),
    CONSTRAINT chk_vagas_ocupadas CHECK (vagas_ocupadas >= 0 AND vagas_ocupadas <= vagas_totais)
);

CREATE TABLE tb_agendamentos_exame (
    id BIGSERIAL PRIMARY KEY,
    horario_exame_id BIGINT NOT NULL REFERENCES tb_horarios_exame_disponiveis(id),
    solicitacao_exame_id BIGINT NOT NULL,
    tipo_exame_id BIGINT NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT chk_agendamento_exame_status CHECK (status IN ('AGENDADO', 'CANCELADO', 'REALIZADO'))
);

CREATE INDEX idx_agendas_exame_tipo ON tb_agendas_exame(tipo_exame_id);
CREATE INDEX idx_horarios_exame_tipo_data ON tb_horarios_exame_disponiveis(tipo_exame_id, data_hora);
CREATE INDEX idx_horarios_exame_vagas ON tb_horarios_exame_disponiveis(tipo_exame_id) WHERE vagas_ocupadas < vagas_totais;
CREATE INDEX idx_agendamentos_exame_horario ON tb_agendamentos_exame(horario_exame_id);
CREATE INDEX idx_agendamentos_exame_solicitacao ON tb_agendamentos_exame(solicitacao_exame_id);
CREATE INDEX idx_agendamentos_exame_data ON tb_agendamentos_exame(data_hora);
