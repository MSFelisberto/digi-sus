package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.AgendaRepository;
import br.com.dgs.agendamento.domain.model.Agenda;
import br.com.dgs.agendamento.domain.model.AgendaId;
import br.com.dgs.agendamento.domain.model.Especialidade;
import br.com.dgs.agendamento.domain.model.MedicoId;
import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendaEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.AgendaJPARepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AgendaRepositoryImpl implements AgendaRepository {

    private final AgendaJPARepository jpaRepository;

    public AgendaRepositoryImpl(AgendaJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Agenda save(Agenda agenda) {
        AgendaEntity entity = toEntity(agenda);
        AgendaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Agenda> findById(AgendaId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Agenda> findByMedicoId(MedicoId medicoId) {
        return jpaRepository.findByMedicoId(medicoId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AgendaEntity toEntity(Agenda agenda) {
        AgendaEntity entity = new AgendaEntity();
        if (agenda.getId() != null) {
            entity.setId(agenda.getId().getValue());
        }
        entity.setMedicoId(agenda.getMedicoId().getValue());
        entity.setDiaSemana(agenda.getDiaSemana().name());
        entity.setHoraInicio(agenda.getHoraInicio());
        entity.setHoraFim(agenda.getHoraFim());
        entity.setDuracaoSlotMinutos(agenda.getDuracaoSlotMinutos());
        entity.setEspecialidade(agenda.getEspecialidade().getValue());
        entity.setAtiva(agenda.isAtiva());
        return entity;
    }

    private Agenda toDomain(AgendaEntity entity) {
        return new Agenda(
                new AgendaId(entity.getId()),
                new MedicoId(entity.getMedicoId()),
                DayOfWeek.valueOf(entity.getDiaSemana()),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getDuracaoSlotMinutos(),
                new Especialidade(entity.getEspecialidade()),
                entity.isAtiva()
        );
    }
}