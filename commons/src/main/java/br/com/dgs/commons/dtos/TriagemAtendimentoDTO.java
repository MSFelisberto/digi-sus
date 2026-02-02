package br.com.dgs.commons.dtos;

import jakarta.validation.constraints.NotNull;

public record TriagemAtendimentoDTO(
        @NotNull
        Long pacienteId,

        @NotNull
        Long triagemId,

        @NotNull
        String dadosClinicos,

        @NotNull
        String conduta
) {
}
