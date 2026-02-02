package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoExameRequestDTO(
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull Long tipoExameId,
        String prioridade,
        String observacoes
) {}