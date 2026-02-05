package br.com.dgs.exames.infrastructure.controllers.dto;

public record AgendaExameResponseDTO(
        Long id,
        Long tipoExameId,
        String diaSemana,
        String horaInicio,
        String horaFim,
        int duracaoSlotMinutos,
        int vagasPorSlot,
        boolean ativa
) {}
