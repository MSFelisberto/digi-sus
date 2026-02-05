package br.com.dgs.exames.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record AgendamentoExameResponseDTO(
        Long id,
        Long solicitacaoExameId,
        Long tipoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}
