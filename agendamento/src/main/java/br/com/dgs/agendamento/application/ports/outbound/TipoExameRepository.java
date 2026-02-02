package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.exame.TipoExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExameId;

import java.util.List;
import java.util.Optional;

public interface TipoExameRepository {
    TipoExame save(TipoExame tipoExame);
    Optional<TipoExame> findById(TipoExameId id);
    List<TipoExame> findAll();
}