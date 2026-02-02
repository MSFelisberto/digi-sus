package br.com.dgs.agendamento.application.dto.exame;

import br.com.dgs.agendamento.application.dto.AuthenticatedUser;

import java.time.LocalDateTime;

public record AgendarExameCommand(
        Long solicitacaoExameId,
        LocalDateTime dataHora,
        AuthenticatedUser currentUser
) {}