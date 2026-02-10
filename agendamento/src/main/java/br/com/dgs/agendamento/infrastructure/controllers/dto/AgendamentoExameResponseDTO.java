package br.com.dgs.agendamento.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record AgendamentoExameResponseDTO(
        Long id,
        Long horarioExameId,
        Long solicitacaoExameId,
        Long tipoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}
