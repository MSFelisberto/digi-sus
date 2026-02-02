package br.com.dgs.agendamento.application.dto;

import java.time.LocalDate;

public record GerarHorariosCommand(
        Long agendaId,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
