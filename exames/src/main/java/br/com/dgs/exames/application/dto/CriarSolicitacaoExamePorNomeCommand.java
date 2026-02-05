package br.com.dgs.exames.application.dto;

public record CriarSolicitacaoExamePorNomeCommand(
        Long pacienteId,
        Long medicoId,
        String tipoExameNome,
        Long atendimentoId,
        Long consultaId,
        String prioridade,
        String observacoes
) {}
