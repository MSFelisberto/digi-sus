package br.com.dgs.atendimento.application.dto;

public record FinalizarAtendimentoCommand(
        Long atendimentoId,
        String anamnese,
        String condutaMedica
) {}
