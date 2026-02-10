package br.com.dgs.agendamento.application.dto;

import java.time.LocalDateTime;

public record HorarioExameDisponivelOutput(
        Long id,
        Long tipoExameId,
        LocalDateTime dataHora,
        int vagasRestantes
) {}
