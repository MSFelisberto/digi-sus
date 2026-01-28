package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.Consulta;
import br.com.dgs.agendamento.domain.model.ConsultaId;
import br.com.dgs.agendamento.domain.model.PacienteId;

import java.util.List;
import java.util.Optional;

public interface ConsultaRepository {
    Consulta save(Consulta consulta);
    Optional<Consulta> findById(ConsultaId id);
    List<Consulta> findByPacienteId(PacienteId pacienteId);
}
