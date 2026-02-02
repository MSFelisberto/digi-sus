package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AgendaExameRequestDTO(
        @NotNull Long tipoExameId,
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim,
        int duracaoSlotMinutos,
        int vagasPorSlot
) {}