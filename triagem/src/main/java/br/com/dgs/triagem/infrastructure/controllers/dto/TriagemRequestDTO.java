package br.com.dgs.triagem.infrastructure.controllers.dto;

import jakarta.validation.constraints.*;

public record TriagemRequestDTO(
        @NotNull(message = "Paciente ID é obrigatório")
        @Positive(message = "Paciente ID deve ser positivo")
        Long pacienteId,

        @NotNull(message = "Funcionário ID é obrigatório")
        @Positive(message = "Funcionário ID deve ser positivo")
        Long funcionarioId,

        @NotBlank(message = "Pressão arterial é obrigatória")
        String pressaoArterial,

        @NotNull(message = "Temperatura é obrigatória")
        @DecimalMin(value = "30.0", message = "Temperatura deve ser no mínimo 30°C")
        @DecimalMax(value = "45.0", message = "Temperatura deve ser no máximo 45°C")
        Double temperatura,

        @NotNull(message = "Batimento cardíaco é obrigatório")
        @Min(value = 30, message = "Batimento cardíaco deve ser no mínimo 30 bpm")
        @Max(value = 250, message = "Batimento cardíaco deve ser no máximo 250 bpm")
        Integer batimentoCardiaco,

        @NotBlank(message = "Conduta é obrigatória")
        String conduta
) {
}
