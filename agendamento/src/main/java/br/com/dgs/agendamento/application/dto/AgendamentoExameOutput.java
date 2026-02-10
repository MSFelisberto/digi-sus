package br.com.dgs.agendamento.application.dto;

import java.time.LocalDateTime;

public record AgendamentoExameOutput(
        Long id,
        Long horarioExameId,
        Long solicitacaoExameId,
        Long tipoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}
