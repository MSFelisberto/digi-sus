package br.com.dgs.historico.application.ports.outbound;

import br.com.dgs.historico.domain.model.ConsultaId;
import br.com.dgs.historico.domain.model.HistoricoConsulta;
import br.com.dgs.historico.domain.model.PacienteId;

import java.util.List;
import java.util.Optional;

public interface HistoricoRepository {
    HistoricoConsulta save(HistoricoConsulta historico);
    Optional<HistoricoConsulta> findByConsultaId(ConsultaId consultaId);
    List<HistoricoConsulta> findByPacienteId(PacienteId pacienteId);
    boolean existsByConsultaId(ConsultaId consultaId);
}
