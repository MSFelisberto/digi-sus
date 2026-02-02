package br.com.dgs.agendamento.application.dto.exame;

public record AgendaExameOutput(
        Long id,
        Long tipoExameId,
        String diaSemana,
        String horaInicio,
        String horaFim,
        int duracaoSlotMinutos,
        int vagasPorSlot,
        boolean ativa
) {}