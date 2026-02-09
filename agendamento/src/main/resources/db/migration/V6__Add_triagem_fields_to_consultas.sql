ALTER TABLE tb_consultas ADD COLUMN tipo_consulta VARCHAR(20) NOT NULL DEFAULT 'REGULAR';
ALTER TABLE tb_consultas ADD COLUMN prioridade VARCHAR(20);
ALTER TABLE tb_consultas ADD COLUMN triagem_id BIGINT;

CREATE INDEX idx_consultas_triagem_id ON tb_consultas(triagem_id) WHERE triagem_id IS NOT NULL;
CREATE INDEX idx_consultas_tipo ON tb_consultas(tipo_consulta);

ALTER TABLE tb_consultas ADD CONSTRAINT chk_tipo_consulta
    CHECK (tipo_consulta IN ('REGULAR', 'ENCAIXE'));
ALTER TABLE tb_consultas ADD CONSTRAINT chk_prioridade
    CHECK (prioridade IS NULL OR prioridade IN ('EMERGENCIA', 'URGENTE', 'POUCO_URGENTE', 'NAO_URGENTE'));
