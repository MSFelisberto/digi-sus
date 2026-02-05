package br.com.dgs.exames.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record SolicitacaoExameResponseDTO(
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
