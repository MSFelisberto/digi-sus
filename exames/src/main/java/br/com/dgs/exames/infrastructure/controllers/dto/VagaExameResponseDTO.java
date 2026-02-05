package br.com.dgs.exames.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record VagaExameResponseDTO(
        LocalDateTime dataHora,
        int vagasRestantes,
        Long tipoExameId
) {}
