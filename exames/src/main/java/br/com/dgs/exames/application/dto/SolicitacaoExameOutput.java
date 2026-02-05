package br.com.dgs.exames.application.dto;

import java.time.LocalDateTime;

public record SolicitacaoExameOutput(
        Long id,
        Long pacienteId,
        Long medicoId,
        Long tipoExameId,
        String tipoExameNome,
        Long atendimentoId,
        Long consultaId,
        String prioridade,
        String observacoes,
        String status,
        LocalDateTime dataCriacao
) {}
