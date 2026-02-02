package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

public record AutoAgendarRequestDTO(
        @NotNull
        Long horarioDisponivelId
) {}