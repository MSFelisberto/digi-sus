package br.com.dgs.historico.application.dto;

import java.time.LocalDateTime;

public record RegistrarAtendimentoFinalizadoCommand(
        Long atendimentoId,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        String anamnese,
        String condutaMedica,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim
) {}
