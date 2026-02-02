package br.com.dgs.agendamento.application.dto.exame;

import java.time.LocalDateTime;

public record AgendamentoExameOutput(
        Long id,
        Long solicitacaoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}