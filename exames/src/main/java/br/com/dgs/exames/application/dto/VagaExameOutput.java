package br.com.dgs.exames.application.dto;

import java.time.LocalDateTime;

public record VagaExameOutput(
        LocalDateTime dataHora,
        int vagasRestantes,
        Long tipoExameId
) {}
