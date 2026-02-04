package br.com.dgs.agendamento.infrastructure.persistence.adapters;

import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.domain.model.*;
import br.com.dgs.agendamento.infrastructure.persistence.entity.ConsultaEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.ConsultaJPARepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ConsultaRepositoryImpl implements ConsultaRepository {

    private final ConsultaJPARepository jpaRepository;

    public ConsultaRepositoryImpl(ConsultaJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Consulta save(Consulta consulta) {
        ConsultaEntity entity = toEntity(consulta);
        ConsultaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Consulta> findById(ConsultaId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Consulta> findByPacienteId(PacienteId pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> findFuturasByPacienteId(PacienteId pacienteId) {
        return jpaRepository.findByPacienteIdAndCanceladaFalseAndDataHoraAfter(
                        pacienteId.getValue(), LocalDateTime.now())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> findFuturasByMedicoId(MedicoId medicoId) {
        return jpaRepository.findByMedicoIdAndCanceladaFalseAndDataHoraAfter(
                        medicoId.getValue(), LocalDateTime.now())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> findAllFuturas() {
        return jpaRepository.findByCanceladaFalseAndDataHoraAfterOrderByDataHoraAsc(
                        LocalDateTime.now())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ConsultaEntity toEntity(Consulta consulta) {
        ConsultaEntity entity = new ConsultaEntity();
        if (consulta.getId() != null) {
            entity.setId(consulta.getId().getValue());
        }
        entity.setPacienteId(consulta.getPacienteId().getValue());
        entity.setMedicoId(consulta.getMedicoId().getValue());
        entity.setDataHora(consulta.getDataHora());
        entity.setEspecialidade(consulta.getEspecialidade().getValue());
        entity.setCancelada(consulta.getStatus() == StatusConsulta.CANCELADA);
        return entity;
    }

    private Consulta toDomain(ConsultaEntity entity) {
        StatusConsulta status = entity.isCancelada() ? StatusConsulta.CANCELADA : StatusConsulta.AGENDADA;

        return new Consulta(
                new ConsultaId(entity.getId()),
                new PacienteId(entity.getPacienteId()),
                new MedicoId(entity.getMedicoId()),
                entity.getDataHora(),
                new Especialidade(entity.getEspecialidade()),
                status
        );
    }
}
