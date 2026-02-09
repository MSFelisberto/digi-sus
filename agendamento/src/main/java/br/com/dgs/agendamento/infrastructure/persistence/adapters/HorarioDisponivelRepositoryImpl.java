package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.HorarioDisponivelRepository;
import br.com.dgs.agendamento.domain.model.*;
import br.com.dgs.agendamento.infrastructure.persistence.entity.HorarioDisponivelEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.HorarioDisponivelJPARepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HorarioDisponivelRepositoryImpl implements HorarioDisponivelRepository {

    private final HorarioDisponivelJPARepository jpaRepository;

    public HorarioDisponivelRepositoryImpl(HorarioDisponivelJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HorarioDisponivel save(HorarioDisponivel horario) {
        HorarioDisponivelEntity entity = toEntity(horario);
        HorarioDisponivelEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void saveAll(List<HorarioDisponivel> horarios) {
        List<HorarioDisponivelEntity> entities = horarios.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<HorarioDisponivel> findById(HorarioDisponivelId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<HorarioDisponivel> findByIdParaReserva(HorarioDisponivelId id) {
        return jpaRepository.findByIdForUpdate(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<HorarioDisponivel> findDisponiveisPorEspecialidadeEPeriodo(
            Especialidade especialidade, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByEspecialidadeAndOcupadoFalseAndDataHoraBetween(
                        especialidade.getValue(), inicio, fim)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<HorarioDisponivel> findByConsultaId(ConsultaId consultaId) {
        return jpaRepository.findByConsultaId(consultaId.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<HorarioDisponivel> findByMedicoIdAndDataHoraParaReserva(
            MedicoId medicoId, LocalDateTime dataHora) {
        return jpaRepository.findByMedicoIdAndDataHoraForUpdate(medicoId.getValue(), dataHora)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<HorarioDisponivel> findPrimeiroDisponivelPorEspecialidadeHoje(
            Especialidade especialidade, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findPrimeiroDisponivelPorEspecialidadeForUpdate(
                        especialidade.getValue(), inicio, fim)
                .map(this::toDomain);
    }

    @Override
    public Optional<MedicoId> findMedicoIdPorEspecialidadeEData(
            Especialidade especialidade, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findPrimeiroMedicoIdPorEspecialidadeEData(
                        especialidade.getValue(), inicio, fim)
                .map(MedicoId::new);
    }

    private HorarioDisponivelEntity toEntity(HorarioDisponivel horario) {
        HorarioDisponivelEntity entity = new HorarioDisponivelEntity();
        if (horario.getId() != null) {
            entity.setId(horario.getId().getValue());
        }
        entity.setAgendaId(horario.getAgendaId().getValue());
        entity.setMedicoId(horario.getMedicoId().getValue());
        entity.setDataHora(horario.getDataHora());
        entity.setEspecialidade(horario.getEspecialidade().getValue());
        entity.setOcupado(horario.isOcupado());
        entity.setConsultaId(horario.getConsultaId() != null ? horario.getConsultaId().getValue() : null);
        return entity;
    }

    private HorarioDisponivel toDomain(HorarioDisponivelEntity entity) {
        return new HorarioDisponivel(
                new HorarioDisponivelId(entity.getId()),
                new AgendaId(entity.getAgendaId()),
                new MedicoId(entity.getMedicoId()),
                entity.getDataHora(),
                new Especialidade(entity.getEspecialidade()),
                entity.isOcupado(),
                entity.getConsultaId() != null ? new ConsultaId(entity.getConsultaId()) : null
        );
    }
}