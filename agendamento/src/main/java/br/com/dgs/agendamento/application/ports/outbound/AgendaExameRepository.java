package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.AgendaExame;
import br.com.dgs.agendamento.domain.model.AgendaExameId;

import java.util.List;
import java.util.Optional;

public interface AgendaExameRepository {
    AgendaExame save(AgendaExame agendaExame);
    Optional<AgendaExame> findById(AgendaExameId id);
    List<AgendaExame> findByTipoExameIdAndAtivaTrue(Long tipoExameId);
}
