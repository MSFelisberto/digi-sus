package br.com.dgs.autenticacao.application.dto;

public record AutenticarCommand(
        String email,
        String senha
) {}

