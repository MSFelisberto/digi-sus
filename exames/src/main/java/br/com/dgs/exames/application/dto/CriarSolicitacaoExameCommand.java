package br.com.dgs.exames.application.dto;

public record CriarSolicitacaoExameCommand(
        Long pacienteId,
        Long medicoId,
        Long tipoExameId,
        Long atendimentoId,
        Long consultaId,
        String prioridade,
        String observacoes
) {}
