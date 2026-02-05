package br.com.dgs.exames.application.dto;

import java.time.LocalDateTime;

public record AgendarExameCommand(
        Long solicitacaoExameId,
        LocalDateTime dataHora,
        AuthenticatedUser currentUser
) {}
