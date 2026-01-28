package br.com.dgs.autenticacao.application.dto;

public record AuthTokenOutput(
        String token,
        String type,
        Long expiresIn,
        String userType
) {}
