package br.com.dgs.exames.application.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CriarAgendaExameCommand(
        Long tipoExameId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        int duracaoSlotMinutos,
        int vagasPorSlot
) {}
