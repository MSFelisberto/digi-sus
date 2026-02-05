package br.com.dgs.atendimento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitarExameRequestDTO(
        @NotBlank(message = "Tipo de exame é obrigatório")
        String tipoExame,
        @NotBlank(message = "Prioridade é obrigatória")
        String prioridade,
        String observacoes
) {}
