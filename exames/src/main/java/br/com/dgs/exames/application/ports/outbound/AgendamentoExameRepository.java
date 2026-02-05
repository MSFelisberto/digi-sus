package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.AgendamentoExame;
import br.com.dgs.exames.domain.model.AgendamentoExameId;
import br.com.dgs.exames.domain.model.TipoExameId;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AgendamentoExameRepository {
    AgendamentoExame save(AgendamentoExame agendamento);
    Optional<AgendamentoExame> findById(AgendamentoExameId id);
    long countByDataHoraAndTipoExameIdAndStatusNot(LocalDateTime dataHora, TipoExameId tipoExameId, String statusExcluido);
}
