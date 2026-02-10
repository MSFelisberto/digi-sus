package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AgendaExameRequestDTO(
        @NotNull Long tipoExameId,
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim,
        @NotNull @Positive int duracaoSlotMinutos,
        @NotNull @Positive int vagasPorSlot
) {}
