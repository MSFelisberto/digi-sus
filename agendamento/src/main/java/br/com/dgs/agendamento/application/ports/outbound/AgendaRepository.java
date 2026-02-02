package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.Agenda;
import br.com.dgs.agendamento.domain.model.AgendaId;
import br.com.dgs.agendamento.domain.model.MedicoId;

import java.util.List;
import java.util.Optional;

public interface AgendaRepository {
    Agenda save(Agenda agenda);
    Optional<Agenda> findById(AgendaId id);
    List<Agenda> findByMedicoId(MedicoId medicoId);
}
