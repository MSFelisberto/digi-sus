package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AgendaRequestDTO(
        @NotNull
        Long medicoId,

        @NotNull
        DayOfWeek diaSemana,

        @NotNull
        LocalTime horaInicio,

        @NotNull
        LocalTime horaFim,

        @NotNull
        @Positive
        int duracaoSlotMinutos,

        @NotBlank
        String especialidade
) {}