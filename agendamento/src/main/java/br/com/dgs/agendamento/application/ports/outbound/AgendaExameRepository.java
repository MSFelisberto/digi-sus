package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.exame.AgendaExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExameId;

import java.util.List;

public interface AgendaExameRepository {
    AgendaExame save(AgendaExame agendaExame);
    List<AgendaExame> findByTipoExameIdAndAtivaTrue(TipoExameId tipoExameId);
}