package br.com.dgs.commons.dtos;

import jakarta.validation.constraints.NotNull;

public record TriagemHistoricoDTO(
        @NotNull
        Long triagemId,

        @NotNull
        Long pacienteId,

        @NotNull
        Long funcionarioId,

        @NotNull
        String pressaoArterial,

        @NotNull
        Double temperatura,

        @NotNull
        Integer batimentoCardiaco,

        @NotNull
        String conduta
) {
}
