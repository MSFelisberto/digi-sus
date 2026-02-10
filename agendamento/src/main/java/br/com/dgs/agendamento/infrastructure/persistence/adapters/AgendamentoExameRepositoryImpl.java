package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.AgendamentoExameRepository;
import br.com.dgs.agendamento.domain.model.*;
import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendamentoExameEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.AgendamentoExameJPARepository;
import org.springframework.stereotype.Component;

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
        return jpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    private AgendamentoExameEntity toEntity(AgendamentoExame agendamento) {
        AgendamentoExameEntity entity = new AgendamentoExameEntity();
        if (agendamento.getId() != null) {
            entity.setId(agendamento.getId().getValue());
        }
        entity.setHorarioExameId(agendamento.getHorarioExameId().getValue());
        entity.setSolicitacaoExameId(agendamento.getSolicitacaoExameId());
        entity.setTipoExameId(agendamento.getTipoExameId());
        entity.setDataHora(agendamento.getDataHora());
        entity.setStatus(agendamento.getStatus().name());
        entity.setDataCriacao(agendamento.getDataCriacao());
        return entity;
    }

    private AgendamentoExame toDomain(AgendamentoExameEntity entity) {
        return new AgendamentoExame(
                new AgendamentoExameId(entity.getId()),
                new HorarioExameDisponivelId(entity.getHorarioExameId()),
                entity.getSolicitacaoExameId(),
                entity.getTipoExameId(),
                entity.getDataHora(),
                StatusAgendamentoExame.valueOf(entity.getStatus()),
                entity.getDataCriacao()
        );
    }
}
