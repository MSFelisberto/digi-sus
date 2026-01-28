package br.com.dgs.historico.application.dto;

public record CancelarHistoricoCommand(
        Long consultaId,
        String motivoCancelamento
) {}
