package br.com.dgs.autenticacao.infrastructure.controllers.dto;

public record AuthResponseDTO(
        String token,
        String type,
        Long expiresIn,
        String userType
) {}
