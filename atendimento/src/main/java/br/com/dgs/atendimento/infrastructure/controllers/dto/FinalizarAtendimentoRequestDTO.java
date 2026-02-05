package br.com.dgs.atendimento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record FinalizarAtendimentoRequestDTO(
        @NotBlank(message = "Anamnese é obrigatória")
        String anamnese,

        @NotBlank(message = "Conduta médica é obrigatória")
        String condutaMedica
) {}
