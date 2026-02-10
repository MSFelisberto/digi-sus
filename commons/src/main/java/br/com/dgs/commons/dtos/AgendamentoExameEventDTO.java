package br.com.dgs.commons.dtos;

import java.time.LocalDateTime;

public record AgendamentoExameEventDTO(
        Long agendamentoExameId,
        Long solicitacaoExameId,
        Long tipoExameId,
        LocalDateTime dataHora,
        String tipoEvento
) {

    public AgendamentoExameEventDTO {
        if (solicitacaoExameId == null || solicitacaoExameId <= 0) {
            throw new IllegalArgumentException("solicitacaoExameId é obrigatório e deve ser positivo");
        }
        if (tipoEvento == null || tipoEvento.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoEvento é obrigatório");
        }
    }
}
