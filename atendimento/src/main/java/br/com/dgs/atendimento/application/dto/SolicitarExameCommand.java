package br.com.dgs.atendimento.application.dto;

public record SolicitarExameCommand(
        Long atendimentoId,
        String tipoExame,
        String prioridade,
        String observacoes
) {}
