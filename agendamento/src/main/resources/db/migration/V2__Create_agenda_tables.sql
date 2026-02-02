CREATE TABLE tb_agendas (
    id BIGSERIAL PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fima TIME NOT NULL,
    duracao_slot_minutos INTEGER NOT NULL,
    especialidade VARCHAR(255) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_dia_semana CHECK (dia_semana IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    CONSTRAINT chk_horario CHECK (hora_fim > hora_inicio),
    CONSTRAINT chk_duracao CHECK (duracao_slot_minutos > 0)
);

CREATE TABLE tb_horarios_disponiveis (
    id BIGSERIAL PRIMARY KEY,
    agenda_id BIGINT NOT NULL REFERENCES tb_agendas(id),
    medico_id BIGINT NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    especialidade VARCHAR(255) NOT NULL,
    ocupado BOOLEAN NOT NULL DEFAULT false,
    consulta_id BIGINT REFERENCES tb_consultas(id),
    CONSTRAINT uk_horario_unico UNIQUE (medico_id, data_hora)
);

CREATE INDEX idx_agendas_medico ON tb_agendas(medico_id);
CREATE INDEX idx_agendas_especialidade ON tb_agendas(especialidade);
CREATE INDEX idx_horarios_especialidade_data ON tb_horarios_disponiveis(especialidade, data_hora);
CREATE INDEX idx_horarios_ocupado ON tb_horarios_disponiveis(ocupado) WHERE NOT ocupado;
CREATE INDEX idx_horarios_medico_data ON tb_horarios_disponiveis(medico_id, data_hora);