package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import java.time.LocalDateTime;

public record AgendamentoExameResponseDTO(
        Long id,
        Long solicitacaoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}