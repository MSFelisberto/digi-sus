package br.com.dgs.agendamento.application.dto.exame;

import java.time.LocalDateTime;

public record VagaExameOutput(
        LocalDateTime dataHora,
        int vagasRestantes,
        Long tipoExameId
) {}