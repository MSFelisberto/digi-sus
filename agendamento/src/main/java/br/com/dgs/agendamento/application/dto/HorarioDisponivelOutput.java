package br.com.dgs.agendamento.application.dto;

import java.time.LocalDateTime;

public record HorarioDisponivelOutput(
        Long id,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade
) {}
