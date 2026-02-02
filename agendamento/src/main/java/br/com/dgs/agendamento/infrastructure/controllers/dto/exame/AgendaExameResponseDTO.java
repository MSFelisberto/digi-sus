package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

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