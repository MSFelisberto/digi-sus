package br.com.dgs.agendamento.application.dto.exame;

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