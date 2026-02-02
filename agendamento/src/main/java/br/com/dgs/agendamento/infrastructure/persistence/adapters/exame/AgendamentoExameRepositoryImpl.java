package br.com.dgs.agendamento.infrastructure.persistence.adapters.exame;

import br.com.dgs.agendamento.application.ports.outbound.AgendamentoExameRepository;
import br.com.dgs.agendamento.domain.model.exame.*;
import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.AgendamentoExameEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.exame.AgendamentoExameJPARepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AgendamentoExameRepositoryImpl implements AgendamentoExameRepository {

    private final AgendamentoExameJPARepository jpaRepository;

    public AgendamentoExameRepositoryImpl(AgendamentoExameJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AgendamentoExame save(AgendamentoExame agendamento) {
        AgendamentoExameEntity entity = toEntity(agendamento);
        AgendamentoExameEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AgendamentoExame> findById(AgendamentoExameId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public long countByDataHoraAndTipoExameIdAndStatusNot(
            LocalDateTime dataHora, TipoExameId tipoExameId, String statusExcluido) {
        return jpaRepository.countByDataHoraAndTipoExameIdAndStatusNot(
                dataHora, tipoExameId.getValue(), statusExcluido);
    }

    private AgendamentoExameEntity toEntity(AgendamentoExame agendamento) {
        AgendamentoExameEntity entity = new AgendamentoExameEntity();
        if (agendamento.getId() != null) {
            entity.setId(agendamento.getId().getValue());
        }
        entity.setSolicitacaoExameId(agendamento.getSolicitacaoExameId().getValue());
        entity.setTipoExameId(agendamento.getTipoExameId().getValue());
        entity.setDataHora(agendamento.getDataHora());
        entity.setStatus(agendamento.getStatus().name());
        entity.setDataCriacao(agendamento.getDataCriacao());
        return entity;
    }

    private AgendamentoExame toDomain(AgendamentoExameEntity entity) {
        return new AgendamentoExame(
                new AgendamentoExameId(entity.getId()),
                new SolicitacaoExameId(entity.getSolicitacaoExameId()),
                new TipoExameId(entity.getTipoExameId()),
                entity.getDataHora(),
                StatusAgendamentoExame.valueOf(entity.getStatus()),
                entity.getDataCriacao()
        );
    }
}