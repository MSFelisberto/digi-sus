package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.Consulta;
import br.com.dgs.agendamento.domain.model.ConsultaId;
import br.com.dgs.agendamento.domain.model.MedicoId;
import br.com.dgs.agendamento.domain.model.PacienteId;

import java.util.List;
import java.util.Optional;

public interface ConsultaRepository {
    Consulta save(Consulta consulta);
    Optional<Consulta> findById(ConsultaId id);
    List<Consulta> findByPacienteId(PacienteId pacienteId);
    List<Consulta> findFuturasByPacienteId(PacienteId pacienteId);
    List<Consulta> findFuturasByMedicoId(MedicoId medicoId);
    List<Consulta> findAllFuturas();
    Optional<Consulta> findByTriagemId(Long triagemId);
}
