package br.com.dgs.agendamento.application.dto.exame;

import java.time.LocalDateTime;

public record SolicitacaoExameOutput(
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