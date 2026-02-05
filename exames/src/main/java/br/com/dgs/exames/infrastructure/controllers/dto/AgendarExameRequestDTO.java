package br.com.dgs.exames.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendarExameRequestDTO(
        @NotNull Long solicitacaoExameId,
        @NotNull LocalDateTime dataHora
) {}
