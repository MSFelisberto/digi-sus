package br.com.dgs.agendamento.application.dto;

public record ListarConsultasQuery(
        Long pacienteId,
        AuthenticatedUser currentUser
) {}
