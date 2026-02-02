package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import java.time.LocalDateTime;

public record VagaExameResponseDTO(
        LocalDateTime dataHora,
        int vagasRestantes,
        Long tipoExameId
) {}