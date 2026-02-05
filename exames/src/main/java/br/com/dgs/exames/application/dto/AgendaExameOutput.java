package br.com.dgs.exames.application.dto;

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
