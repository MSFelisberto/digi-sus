package br.com.dgs.autenticacao.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeRequestDTO(
        @NotBlank(message = "Nome da especialidade é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "Código da especialidade é obrigatório")
        @Size(max = 10, message = "Código deve ter no máximo 10 caracteres")
        String codigo
) {}
