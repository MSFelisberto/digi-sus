package br.com.dgs.atendimento.application.ports.outbound;

import br.com.dgs.atendimento.domain.model.Atendimento;
import br.com.dgs.atendimento.domain.model.AtendimentoId;
import br.com.dgs.atendimento.domain.model.ConsultaId;

import java.util.Optional;

public interface AtendimentoRepository {
    Atendimento save(Atendimento atendimento);
    Optional<Atendimento> findById(AtendimentoId id);
    Optional<Atendimento> findByConsultaId(ConsultaId consultaId);
    boolean existsByConsultaId(ConsultaId consultaId);
}
