package br.com.dgs.historico.infrastructure.persistence.adapters;

import br.com.dgs.historico.application.ports.outbound.HistoricoRepository;
import br.com.dgs.historico.domain.model.*;
import br.com.dgs.historico.infrastructure.persistence.entity.HistoricoEntity;
import br.com.dgs.historico.infrastructure.persistence.repository.HistoricoJPARepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HistoricoRepositoryImpl implements HistoricoRepository {

    private final HistoricoJPARepository jpaRepository;

    public HistoricoRepositoryImpl(HistoricoJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HistoricoConsulta save(HistoricoConsulta historico) {
        HistoricoEntity entity = toEntity(historico);
        HistoricoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<HistoricoConsulta> findByConsultaId(ConsultaId consultaId) {
        return jpaRepository.findByConsultaId(consultaId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<HistoricoConsulta> findByPacienteId(PacienteId pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByConsultaId(ConsultaId consultaId) {
        return jpaRepository.existsByConsultaId(consultaId.getValue());
    }

    private HistoricoEntity toEntity(HistoricoConsulta historico) {
        HistoricoEntity entity = new HistoricoEntity();

        if (historico.getId() != null) {
            entity.setId(historico.getId().getValue());
        }

        entity.setConsultaId(historico.getConsultaId().getValue());
        entity.setPacienteId(historico.getPacienteId().getValue());
        entity.setMedicoId(historico.getMedicoId().getValue());
        entity.setDataHora(historico.getDataHora());
        entity.setEspecialidade(historico.getEspecialidade());
        entity.setStatus(historico.getStatus().name());
        entity.setObservacoes(historico.getObservacoes());
        entity.setDataCriacao(historico.getDataCriacao());
        entity.setDataAtualizacao(historico.getDataAtualizacao());

        return entity;
    }

    private HistoricoConsulta toDomain(HistoricoEntity entity) {
        return new HistoricoConsulta(
                new HistoricoId(entity.getId()),
                new ConsultaId(entity.getConsultaId()),
                new PacienteId(entity.getPacienteId()),
                new MedicoId(entity.getMedicoId()),
                entity.getDataHora(),
                entity.getEspecialidade(),
                StatusHistorico.valueOf(entity.getStatus()),
                entity.getObservacoes(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}
