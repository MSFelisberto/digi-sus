package br.com.dgs.agendamento.application.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CriarAgendaCommand(
        Long medicoId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        int duracaoSlotMinutos,
        String especialidade
) {}
