package br.com.dgs.agendamento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GerarHorariosRequestDTO(
        @NotNull
        LocalDate dataInicio,

        @NotNull
        LocalDate dataFim
) {}