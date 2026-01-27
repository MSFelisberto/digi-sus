package br.com.dgs.commons.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HistoricoEventDTO(
        @NotNull
        Long consultaId,

        @NotNull
        Long pacienteId,

        @NotNull
        Long medicoId,

        @NotNull
        LocalDateTime dataHora,

        @NotNull
        String especialidade,

        String tipoEvento

) {

    public HistoricoEventDTO {
        if (consultaId == null || consultaId <= 0) {
            throw new IllegalArgumentException("consultaId é obrigatório e deve ser positivo");
        }
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("pacienteId é obrigatório e deve ser positivo");
        }
        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException("medicoId é obrigatório e deve ser positivo");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("dataHora é obrigatória");
        }
        if (especialidade == null || especialidade.trim().isEmpty()) {
            throw new IllegalArgumentException("especialidade é obrigatória");
        }
    }
}
