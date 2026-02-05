package br.com.dgs.exames.application.dto;

public record ListarSolicitacoesQuery(
        Long pacienteId,
        Long atendimentoId,
        AuthenticatedUser currentUser
) {}
