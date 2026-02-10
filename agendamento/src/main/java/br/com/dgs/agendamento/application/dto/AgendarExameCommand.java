package br.com.dgs.agendamento.application.dto;

public record AgendarExameCommand(
        Long horarioExameDisponivelId,
        Long solicitacaoExameId
) {}
