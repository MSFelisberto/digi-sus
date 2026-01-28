package br.com.dgs.historico.application.dto;

import java.time.LocalDateTime;

public record HistoricoOutput(
        Long id,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade,
        String status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {}
