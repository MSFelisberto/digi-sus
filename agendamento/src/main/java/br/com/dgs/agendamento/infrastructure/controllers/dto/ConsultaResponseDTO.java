package br.com.dgs.agendamento.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record ConsultaResponseDTO(
        Long id,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade,
        String status,
        String tipoConsulta,
        String prioridade,
        Long triagemId
) {
}