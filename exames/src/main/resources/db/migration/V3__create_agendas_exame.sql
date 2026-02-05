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
