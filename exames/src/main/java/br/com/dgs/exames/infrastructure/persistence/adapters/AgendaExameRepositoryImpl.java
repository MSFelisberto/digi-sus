package br.com.dgs.exames.infrastructure.persistence.adapters;

import br.com.dgs.exames.application.ports.outbound.AgendaExameRepository;
import br.com.dgs.exames.domain.model.*;
import br.com.dgs.exames.infrastructure.persistence.entity.AgendaExameEntity;
import br.com.dgs.exames.infrastructure.persistence.repository.AgendaExameJPARepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgendaExameRepositoryImpl implements AgendaExameRepository {

    private final AgendaExameJPARepository jpaRepository;

    public AgendaExameRepositoryImpl(AgendaExameJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AgendaExame save(AgendaExame agendaExame) {
        AgendaExameEntity entity = toEntity(agendaExame);
        AgendaExameEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AgendaExame> findByTipoExameIdAndAtivaTrue(TipoExameId tipoExameId) {
        return jpaRepository.findByTipoExameIdAndAtivaTrue(tipoExameId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AgendaExameEntity toEntity(AgendaExame agenda) {
        AgendaExameEntity entity = new AgendaExameEntity();
        if (agenda.getId() != null) {
            entity.setId(agenda.getId().getValue());
        }
        entity.setTipoExameId(agenda.getTipoExameId().getValue());
        entity.setDiaSemana(agenda.getDiaSemana().name());
        entity.setHoraInicio(agenda.getHoraInicio());
        entity.setHoraFim(agenda.getHoraFim());
        entity.setDuracaoSlotMinutos(agenda.getDuracaoSlotMinutos());
        entity.setVagasPorSlot(agenda.getVagasPorSlot());
        entity.setAtiva(agenda.isAtiva());
        return entity;
    }

    private AgendaExame toDomain(AgendaExameEntity entity) {
        return new AgendaExame(
                new AgendaExameId(entity.getId()),
                new TipoExameId(entity.getTipoExameId()),
                DayOfWeek.valueOf(entity.getDiaSemana()),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getDuracaoSlotMinutos(),
                entity.getVagasPorSlot(),
                entity.isAtiva()
        );
    }
}
