package br.com.dgs.agendamento.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record HorarioDisponivelResponseDTO(
        Long id,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade
) {}