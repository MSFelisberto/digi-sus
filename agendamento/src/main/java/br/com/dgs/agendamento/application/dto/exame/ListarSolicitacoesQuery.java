package br.com.dgs.agendamento.application.dto.exame;

import br.com.dgs.agendamento.application.dto.AuthenticatedUser;

public record ListarSolicitacoesQuery(
        Long pacienteId,
        AuthenticatedUser currentUser
) {}