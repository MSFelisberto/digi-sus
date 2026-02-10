-- Adicionar coluna status
ALTER TABLE tb_consultas ADD COLUMN status VARCHAR(20);

-- Migrar dados existentes
UPDATE tb_consultas SET status = 'CANCELADA' WHERE cancelada = true;
UPDATE tb_consultas SET status = 'AGENDADA' WHERE cancelada = false;

-- Tornar NOT NULL e adicionar default
ALTER TABLE tb_consultas ALTER COLUMN status SET NOT NULL;
ALTER TABLE tb_consultas ALTER COLUMN status SET DEFAULT 'AGENDADA';

-- Remover coluna antiga
ALTER TABLE tb_consultas DROP COLUMN cancelada;

-- Constraint de valores válidos
ALTER TABLE tb_consultas ADD CONSTRAINT chk_status_consulta
    CHECK (status IN ('AGENDADA', 'EM_ATENDIMENTO', 'REALIZADA', 'CANCELADA'));

-- Índice para queries por status
CREATE INDEX idx_consultas_status ON tb_consultas(status);
