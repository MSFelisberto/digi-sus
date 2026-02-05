package br.com.dgs.exames.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoExameRequestDTO(
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull Long tipoExameId,
        Long atendimentoId,
        Long consultaId,
        String prioridade,
        String observacoes
) {}
