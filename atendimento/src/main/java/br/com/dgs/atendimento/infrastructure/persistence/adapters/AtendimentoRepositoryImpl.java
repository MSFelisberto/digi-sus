package br.com.dgs.atendimento.infrastructure.persistence.adapters;

import br.com.dgs.atendimento.application.ports.outbound.AtendimentoRepository;
import br.com.dgs.atendimento.domain.model.*;
import br.com.dgs.atendimento.infrastructure.persistence.entity.AtendimentoEntity;
import br.com.dgs.atendimento.infrastructure.persistence.repository.AtendimentoJPARepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AtendimentoRepositoryImpl implements AtendimentoRepository {

    private final AtendimentoJPARepository jpaRepository;

    public AtendimentoRepositoryImpl(AtendimentoJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Atendimento save(Atendimento atendimento) {
        AtendimentoEntity entity = toEntity(atendimento);
        AtendimentoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Atendimento> findById(AtendimentoId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<Atendimento> findByConsultaId(ConsultaId consultaId) {
        return jpaRepository.findByConsultaId(consultaId.getValue())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByConsultaId(ConsultaId consultaId) {
        return jpaRepository.existsByConsultaId(consultaId.getValue());
    }

    private AtendimentoEntity toEntity(Atendimento atendimento) {
        AtendimentoEntity entity = new AtendimentoEntity();

        if (atendimento.getId() != null) {
            entity.setId(atendimento.getId().getValue());
        }

        entity.setConsultaId(atendimento.getConsultaId().getValue());
        entity.setPacienteId(atendimento.getPacienteId().getValue());
        entity.setMedicoId(atendimento.getMedicoId().getValue());
        entity.setAnamnese(atendimento.getAnamnese() != null ? atendimento.getAnamnese().getValue() : null);
        entity.setCondutaMedica(atendimento.getCondutaMedica() != null ? atendimento.getCondutaMedica().getValue() : null);
        entity.setDataHoraInicio(atendimento.getDataHoraInicio());
        entity.setDataHoraFim(atendimento.getDataHoraFim());
        entity.setStatus(atendimento.getStatus().name());

        return entity;
    }

    private Atendimento toDomain(AtendimentoEntity entity) {
        return new Atendimento(
                new AtendimentoId(entity.getId()),
                new ConsultaId(entity.getConsultaId()),
                new PacienteId(entity.getPacienteId()),
                new MedicoId(entity.getMedicoId()),
                entity.getAnamnese() != null ? new Anamnese(entity.getAnamnese()) : null,
                entity.getCondutaMedica() != null ? new CondutaMedica(entity.getCondutaMedica()) : null,
                entity.getDataHoraInicio(),
                entity.getDataHoraFim(),
                StatusAtendimento.valueOf(entity.getStatus())
        );
    }
}
