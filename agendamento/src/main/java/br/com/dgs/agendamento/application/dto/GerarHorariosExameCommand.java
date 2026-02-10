package br.com.dgs.agendamento.application.dto;

import java.time.LocalDate;

public record GerarHorariosExameCommand(
        Long agendaExameId,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
