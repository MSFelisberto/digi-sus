package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

import java.time.LocalDateTime;

public record SolicitacaoExameResponseDTO(
        Long id,
        Long pacienteId,
        Long medicoId,
        Long tipoExameId,
        String tipoExameNome,
        String prioridade,
        String observacoes,
        String status,
        LocalDateTime dataCriacao
) {}