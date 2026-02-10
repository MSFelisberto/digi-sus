package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

public record AgendarExameRequestDTO(
        @NotNull Long horarioExameDisponivelId,
        @NotNull Long solicitacaoExameId
) {}
