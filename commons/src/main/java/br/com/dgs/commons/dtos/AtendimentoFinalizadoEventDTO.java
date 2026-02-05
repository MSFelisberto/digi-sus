package br.com.dgs.commons.dtos;

import java.time.LocalDateTime;

public record AtendimentoFinalizadoEventDTO(
        Long atendimentoId,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        String anamnese,
        String condutaMedica,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim
) {

    public AtendimentoFinalizadoEventDTO {
        if (atendimentoId == null || atendimentoId <= 0) {
            throw new IllegalArgumentException("atendimentoId é obrigatório e deve ser positivo");
        }
        if (consultaId == null || consultaId <= 0) {
            throw new IllegalArgumentException("consultaId é obrigatório e deve ser positivo");
        }
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("pacienteId é obrigatório e deve ser positivo");
        }
        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException("medicoId é obrigatório e deve ser positivo");
        }
        if (anamnese == null || anamnese.trim().isEmpty()) {
            throw new IllegalArgumentException("anamnese é obrigatória");
        }
        if (condutaMedica == null || condutaMedica.trim().isEmpty()) {
            throw new IllegalArgumentException("condutaMedica é obrigatória");
        }
        if (dataHoraInicio == null) {
            throw new IllegalArgumentException("dataHoraInicio é obrigatória");
        }
        if (dataHoraFim == null) {
            throw new IllegalArgumentException("dataHoraFim é obrigatória");
        }
    }
}
