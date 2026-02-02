package br.com.dgs.commons.dtos;

import java.time.LocalDateTime;

public record ExameEventDTO(
        Long solicitacaoExameId,
        Long pacienteId,
        Long medicoId,
        String tipoExameNome,
        String prioridade,
        LocalDateTime dataHora,
        String tipoEvento
) {

    public ExameEventDTO {
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("pacienteId é obrigatório e deve ser positivo");
        }

        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException("medicoId é obrigatório e deve ser positivo");
        }

        if (tipoExameNome == null || tipoExameNome.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoExameNome é obrigatório");
        }

        if (tipoEvento == null || tipoEvento.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoEvento é obrigatório");
        }
    }
}
