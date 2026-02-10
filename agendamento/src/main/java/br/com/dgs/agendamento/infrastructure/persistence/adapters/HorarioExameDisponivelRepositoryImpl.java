package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.HorarioExameDisponivelRepository;
import br.com.dgs.agendamento.domain.model.AgendaExameId;
import br.com.dgs.agendamento.domain.model.HorarioExameDisponivel;
import br.com.dgs.agendamento.domain.model.HorarioExameDisponivelId;
import br.com.dgs.agendamento.infrastructure.persistence.entity.HorarioExameDisponivelEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.HorarioExameDisponivelJPARepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HorarioExameDisponivelRepositoryImpl implements HorarioExameDisponivelRepository {

    private final HorarioExameDisponivelJPARepository jpaRepository;

    public HorarioExameDisponivelRepositoryImpl(HorarioExameDisponivelJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HorarioExameDisponivel save(HorarioExameDisponivel horario) {
        HorarioExameDisponivelEntity entity = toEntity(horario);
        HorarioExameDisponivelEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void saveAll(List<HorarioExameDisponivel> horarios) {
        List<HorarioExameDisponivelEntity> entities = horarios.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public Optional<HorarioExameDisponivel> findByIdParaReserva(HorarioExameDisponivelId id) {
        return jpaRepository.findByIdForUpdate(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<HorarioExameDisponivel> findDisponiveisPorTipoExameEPeriodo(
            Long tipoExameId, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findDisponiveisPorTipoExameEPeriodo(tipoExameId, inicio, fim)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private HorarioExameDisponivelEntity toEntity(HorarioExameDisponivel horario) {
        HorarioExameDisponivelEntity entity = new HorarioExameDisponivelEntity();
        if (horario.getId() != null) {
            entity.setId(horario.getId().getValue());
        }
        entity.setAgendaExameId(horario.getAgendaExameId().getValue());
        entity.setTipoExameId(horario.getTipoExameId());
        entity.setDataHora(horario.getDataHora());
        entity.setVagasTotais(horario.getVagasTotais());
        entity.setVagasOcupadas(horario.getVagasOcupadas());
        return entity;
    }

    private HorarioExameDisponivel toDomain(HorarioExameDisponivelEntity entity) {
        return new HorarioExameDisponivel(
                new HorarioExameDisponivelId(entity.getId()),
                new AgendaExameId(entity.getAgendaExameId()),
                entity.getTipoExameId(),
                entity.getDataHora(),
                entity.getVagasTotais(),
                entity.getVagasOcupadas()
        );
    }
}
