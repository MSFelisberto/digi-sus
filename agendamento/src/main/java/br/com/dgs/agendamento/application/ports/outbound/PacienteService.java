package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.PacienteId;

public interface PacienteService {
    boolean exists(PacienteId pacienteId);
}
