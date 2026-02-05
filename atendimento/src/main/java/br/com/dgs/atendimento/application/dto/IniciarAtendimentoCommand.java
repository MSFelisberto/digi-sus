package br.com.dgs.atendimento.application.dto;

public record IniciarAtendimentoCommand(
        Long consultaId,
        Long medicoId
) {}
