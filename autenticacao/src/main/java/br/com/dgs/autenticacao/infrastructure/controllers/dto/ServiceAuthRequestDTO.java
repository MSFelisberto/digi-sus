package br.com.dgs.autenticacao.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record ServiceAuthRequestDTO(
        @NotBlank(message = "Service ID é obrigatório")
        String serviceId,

        @NotBlank(message = "Service Secret é obrigatório")
        String serviceSecret
) {}