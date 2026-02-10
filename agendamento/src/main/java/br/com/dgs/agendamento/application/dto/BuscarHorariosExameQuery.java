package br.com.dgs.agendamento.application.dto;

import java.time.LocalDate;

public record BuscarHorariosExameQuery(
        Long tipoExameId,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
