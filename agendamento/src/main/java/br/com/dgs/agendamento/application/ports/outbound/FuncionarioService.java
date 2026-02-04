package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.MedicoId;

public interface FuncionarioService {
    boolean isMedico(MedicoId funcionarioId);
}
