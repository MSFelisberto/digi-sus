package br.com.dgs.historico.application.dto;

import java.time.LocalDateTime;

public record AtualizarHistoricoCommand(
        Long consultaId,
        LocalDateTime novaDataHora,
        Long novoMedicoId,
        String novaEspecialidade
) {}
