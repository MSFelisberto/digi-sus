package br.com.dgs.commons.dtos;

public record SolicitacaoExameEventDTO(
        Long atendimentoId,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        String tipoExame,
        String prioridade,
        String observacoes
) {

    public SolicitacaoExameEventDTO {
        if (atendimentoId == null || atendimentoId <= 0) {
            throw new IllegalArgumentException("atendimentoId é obrigatório e deve ser positivo");
        }
        if (consultaId == null || consultaId <= 0) {
            throw new IllegalArgumentException("consultaId é obrigatório e deve ser positivo");
        }
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("pacienteId é obrigatório e deve ser positivo");
        }
        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException("medicoId é obrigatório e deve ser positivo");
        }
        if (tipoExame == null || tipoExame.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoExame é obrigatório");
        }
        if (prioridade == null || prioridade.trim().isEmpty()) {
            throw new IllegalArgumentException("prioridade é obrigatória");
        }
    }
}
