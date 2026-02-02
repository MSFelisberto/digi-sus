package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.PacienteId;
import br.com.dgs.agendamento.domain.model.exame.SolicitacaoExame;
import br.com.dgs.agendamento.domain.model.exame.SolicitacaoExameId;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoExameRepository {
    SolicitacaoExame save(SolicitacaoExame solicitacao);
    Optional<SolicitacaoExame> findById(SolicitacaoExameId id);
    List<SolicitacaoExame> findByPacienteId(PacienteId pacienteId);
}