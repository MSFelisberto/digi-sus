package br.com.dgs.notificacoes.application.usecases;

import java.time.LocalDateTime;

public record ConsultaInput(
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String especialidade
) {}