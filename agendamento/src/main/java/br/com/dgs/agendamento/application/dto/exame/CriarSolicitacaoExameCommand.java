package br.com.dgs.agendamento.application.dto.exame;

public record CriarSolicitacaoExameCommand(
        Long pacienteId,
        Long medicoId,
        Long tipoExameId,
        String prioridade,
        String observacoes
) {}