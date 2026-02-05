package br.com.dgs.exames.application.dto;

import java.time.LocalDateTime;

public record AgendamentoExameOutput(
        Long id,
        Long solicitacaoExameId,
        Long tipoExameId,
        LocalDateTime dataHora,
        String status,
        LocalDateTime dataCriacao
) {}
