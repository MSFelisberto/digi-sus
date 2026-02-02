package br.com.dgs.agendamento.application.dto;

public record AutoAgendarCommand(
        Long horarioDisponivelId,
        AuthenticatedUser currentUser
) {}
