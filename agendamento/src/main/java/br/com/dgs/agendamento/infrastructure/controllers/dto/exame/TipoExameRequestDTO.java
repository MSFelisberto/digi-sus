package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import jakarta.validation.constraints.NotBlank;

public record TipoExameRequestDTO(
        @NotBlank String nome,
        @NotBlank String codigo,
        String descricao,
        String preparacao
) {}