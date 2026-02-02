package br.com.dgs.agendamento.application.dto;

import java.time.LocalDate;

public record BuscarHorariosQuery(
        String especialidade,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
