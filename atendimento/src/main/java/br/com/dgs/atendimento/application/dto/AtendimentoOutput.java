package br.com.dgs.atendimento.application.dto;

import java.time.LocalDateTime;

public record AtendimentoOutput(
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
