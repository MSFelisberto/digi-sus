-- Remove a tabela de exames solicitados pois exames agora são gerenciados pelo exames-service
DROP INDEX IF EXISTS idx_exames_atendimento;
DROP TABLE IF EXISTS tb_exames_solicitados;
