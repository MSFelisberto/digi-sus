package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.PacienteId;

public interface PacienteService {
    boolean exists(PacienteId pacienteId);
}
