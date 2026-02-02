package br.com.dgs.agendamento.application.dto;

public record AgendaOutput(
        Long id,
        Long medicoId,
        String diaSemana,
        String horaInicio,
        String horaFim,
        int duracaoSlotMinutos,
        String especialidade,
        boolean ativa
) {}
