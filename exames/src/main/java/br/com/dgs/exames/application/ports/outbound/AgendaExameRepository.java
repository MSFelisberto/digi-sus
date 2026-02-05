package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.AgendaExame;
import br.com.dgs.exames.domain.model.TipoExameId;

import java.util.List;

public interface AgendaExameRepository {
    AgendaExame save(AgendaExame agendaExame);
    List<AgendaExame> findByTipoExameIdAndAtivaTrue(TipoExameId tipoExameId);
}
