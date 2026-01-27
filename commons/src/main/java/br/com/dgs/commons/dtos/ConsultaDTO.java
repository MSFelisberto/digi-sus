package br.com.dgs.commons.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaDTO(
        @NotNull
        Long pacienteId,

        @NotNull
        Long medicoId,

        @NotNull
        @Future
        LocalDateTime dataHora,

        @NotBlank
        String especialidade
) {
}
