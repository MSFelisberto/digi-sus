package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendarExameRequestDTO(
        @NotNull Long solicitacaoExameId,
        @NotNull LocalDateTime dataHora
) {}