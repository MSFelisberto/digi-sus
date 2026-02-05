package br.com.dgs.exames.application.dto;

import java.time.LocalDate;

public record BuscarVagasExameQuery(
        Long tipoExameId,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
