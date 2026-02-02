package br.com.dgs.agendamento.application.dto.exame;

import java.time.LocalDate;

public record BuscarVagasExameQuery(
        Long tipoExameId,
        LocalDate dataInicio,
        LocalDate dataFim
) {}