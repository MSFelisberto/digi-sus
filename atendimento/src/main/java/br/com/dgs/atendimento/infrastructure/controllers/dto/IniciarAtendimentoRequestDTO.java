package br.com.dgs.atendimento.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotNull;

public record IniciarAtendimentoRequestDTO(
        @NotNull(message = "ID da consulta é obrigatório")
        Long consultaId
) {}
