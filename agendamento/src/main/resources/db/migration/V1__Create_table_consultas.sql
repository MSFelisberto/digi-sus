CREATE TABLE tb_consultas (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    especialidade VARCHAR(255) NOT NULL,
    cancelada BOOLEAN NOT NULL DEFAULT false
);