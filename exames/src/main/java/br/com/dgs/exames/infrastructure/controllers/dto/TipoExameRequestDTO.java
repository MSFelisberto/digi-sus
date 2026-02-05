package br.com.dgs.exames.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoExameRequestDTO(
        @NotBlank String nome,
        @NotBlank String codigo,
        String descricao,
        String preparacao
) {}
