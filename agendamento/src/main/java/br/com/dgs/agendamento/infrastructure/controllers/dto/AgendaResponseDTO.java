package br.com.dgs.agendamento.infrastructure.controllers.dto;

public record AgendaResponseDTO(
        Long id,
        Long medicoId,
        String diaSemana,
        String horaInicio,
        String horaFim,
        int duracaoSlotMinutos,
        String especialidade,
        boolean ativa
) {}