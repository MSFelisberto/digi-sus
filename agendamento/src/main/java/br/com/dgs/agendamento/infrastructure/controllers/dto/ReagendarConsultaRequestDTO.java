package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReagendarConsultaRequestDTO(
        @NotNull
        Long medicoId,

        @NotNull
        @Future
        LocalDateTime dataHora,

        @NotBlank
        String especialidade
) {
}