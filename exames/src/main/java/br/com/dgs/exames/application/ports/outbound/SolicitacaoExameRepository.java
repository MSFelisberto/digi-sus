package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.AtendimentoId;
import br.com.dgs.exames.domain.model.PacienteId;
import br.com.dgs.exames.domain.model.SolicitacaoExame;
import br.com.dgs.exames.domain.model.SolicitacaoExameId;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoExameRepository {
    SolicitacaoExame save(SolicitacaoExame solicitacao);
    Optional<SolicitacaoExame> findById(SolicitacaoExameId id);
    List<SolicitacaoExame> findByPacienteId(PacienteId pacienteId);
    List<SolicitacaoExame> findByAtendimentoId(AtendimentoId atendimentoId);
}
