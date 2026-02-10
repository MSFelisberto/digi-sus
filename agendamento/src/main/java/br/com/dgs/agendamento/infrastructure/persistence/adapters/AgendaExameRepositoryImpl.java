package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.AgendaExameRepository;
import br.com.dgs.agendamento.domain.model.AgendaExame;
import br.com.dgs.agendamento.domain.model.AgendaExameId;
import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendaExameEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.AgendaExameJPARepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AgendaExameRepositoryImpl implements AgendaExameRepository {

    private final AgendaExameJPARepository jpaRepository;

    public AgendaExameRepositoryImpl(AgendaExameJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AgendaExame save(AgendaExame agenda) {
        AgendaExameEntity entity = toEntity(agenda);
        AgendaExameEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AgendaExame> findById(AgendaExameId id) {
        return jpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<AgendaExame> findByTipoExameIdAndAtivaTrue(Long tipoExameId) {
        return jpaRepository.findByTipoExameIdAndAtivaTrue(tipoExameId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AgendaExameEntity toEntity(AgendaExame agenda) {
        AgendaExameEntity entity = new AgendaExameEntity();
        if (agenda.getId() != null) {
            entity.setId(agenda.getId().getValue());
        }
        entity.setTipoExameId(agenda.getTipoExameId());
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
                entity.getTipoExameId(),
                DayOfWeek.valueOf(entity.getDiaSemana()),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getDuracaoSlotMinutos(),
                entity.getVagasPorSlot(),
                entity.isAtiva()
        );
    }
}
