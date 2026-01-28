package br.com.dgs.agendamento.application.dto;

import java.time.LocalDateTime;

public record AgendarConsultaCommand(
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade
) {}
