package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.AgendamentoExame;
import br.com.dgs.agendamento.domain.model.AgendamentoExameId;

import java.util.Optional;

public interface AgendamentoExameRepository {
    AgendamentoExame save(AgendamentoExame agendamento);
    Optional<AgendamentoExame> findById(AgendamentoExameId id);
}
