package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.exame.AgendamentoExame;
import br.com.dgs.agendamento.domain.model.exame.AgendamentoExameId;
import br.com.dgs.agendamento.domain.model.exame.TipoExameId;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AgendamentoExameRepository {
    AgendamentoExame save(AgendamentoExame agendamento);
    Optional<AgendamentoExame> findById(AgendamentoExameId id);
    long countByDataHoraAndTipoExameIdAndStatusNot(LocalDateTime dataHora, TipoExameId tipoExameId, String statusExcluido);
}