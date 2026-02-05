package br.com.dgs.atendimento.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record AtendimentoResponseDTO(
        Long id,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        String anamnese,
        String condutaMedica,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String status
) {}
