package br.com.dgs.historico.application.dto;

public record ListarHistoricoQuery(
        Long pacienteId,
        AuthenticatedUser currentUser
) {}
