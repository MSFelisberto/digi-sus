CREATE TABLE tb_tipos_exame (
                                id BIGSERIAL PRIMARY KEY,
                                nome VARCHAR(255) NOT NULL,
                                codigo VARCHAR(50) NOT NULL UNIQUE,
                                descricao TEXT,
                                preparacao TEXT,
                                ativo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE tb_solicitacoes_exame (
                                       id BIGSERIAL PRIMARY KEY,
                                       paciente_id BIGINT NOT NULL,
                                       medico_id BIGINT NOT NULL,
                                       tipo_exame_id BIGINT NOT NULL REFERENCES tb_tipos_exame(id),
                                       prioridade VARCHAR(20) NOT NULL CHECK (prioridade IN ('NORMAL', 'URGENTE')),
                                       observacoes TEXT,
                                       status VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'AGENDADA', 'REALIZADA', 'CANCELADA')),
                                       data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE tb_agendas_exame (
                                  id BIGSERIAL PRIMARY KEY,
                                  tipo_exame_id BIGINT NOT NULL REFERENCES tb_tipos_exame(id),
                                  dia_semana VARCHAR(20) NOT NULL,
                                  hora_inicio TIME NOT NULL,
                                  hora_fim TIME NOT NULL,
                                  duracao_slot_minutos INTEGER NOT NULL,
                                  vagas_por_slot INTEGER NOT NULL,
                                  ativa BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE tb_agendamentos_exame (
                                       id BIGSERIAL PRIMARY KEY,
                                       solicitacao_exame_id BIGINT NOT NULL REFERENCES tb_solicitacoes_exame(id),
                                       tipo_exame_id BIGINT NOT NULL REFERENCES tb_tipos_exame(id),
                                       data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                       status VARCHAR(20) NOT NULL CHECK (status IN ('AGENDADO', 'CANCELADO', 'REALIZADO')),
                                       data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_solicitacoes_paciente ON tb_solicitacoes_exame(paciente_id);
CREATE INDEX idx_solicitacoes_tipo_exame ON tb_solicitacoes_exame(tipo_exame_id);
CREATE INDEX idx_agendas_exame_tipo ON tb_agendas_exame(tipo_exame_id);
CREATE INDEX idx_agendamentos_exame_data ON tb_agendamentos_exame(data_hora);
CREATE INDEX idx_agendamentos_exame_solicitacao ON tb_agendamentos_exame(solicitacao_exame_id);