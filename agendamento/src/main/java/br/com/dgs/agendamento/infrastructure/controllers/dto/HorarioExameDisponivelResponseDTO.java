package br.com.dgs.agendamento.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record HorarioExameDisponivelResponseDTO(
        Long id,
        Long tipoExameId,
        LocalDateTime dataHora,
        int vagasRestantes
) {}
