package br.com.dgs.exames.infrastructure.persistence.adapters;

import br.com.dgs.exames.application.ports.outbound.SolicitacaoExameRepository;
import br.com.dgs.exames.domain.model.*;
import br.com.dgs.exames.infrastructure.persistence.entity.SolicitacaoExameEntity;
import br.com.dgs.exames.infrastructure.persistence.repository.SolicitacaoExameJPARepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SolicitacaoExameRepositoryImpl implements SolicitacaoExameRepository {

    private final SolicitacaoExameJPARepository jpaRepository;

    public SolicitacaoExameRepositoryImpl(SolicitacaoExameJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SolicitacaoExame save(SolicitacaoExame solicitacao) {
        SolicitacaoExameEntity entity = toEntity(solicitacao);
        SolicitacaoExameEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SolicitacaoExame> findById(SolicitacaoExameId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<SolicitacaoExame> findByPacienteId(PacienteId pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitacaoExame> findByAtendimentoId(AtendimentoId atendimentoId) {
        return jpaRepository.findByAtendimentoId(atendimentoId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private SolicitacaoExameEntity toEntity(SolicitacaoExame solicitacao) {
        SolicitacaoExameEntity entity = new SolicitacaoExameEntity();
        if (solicitacao.getId() != null) {
            entity.setId(solicitacao.getId().getValue());
        }
        entity.setPacienteId(solicitacao.getPacienteId().getValue());
        entity.setMedicoId(solicitacao.getMedicoId().getValue());
        entity.setTipoExameId(solicitacao.getTipoExameId().getValue());
        entity.setAtendimentoId(solicitacao.getAtendimentoId() != null ? solicitacao.getAtendimentoId().getValue() : null);
        entity.setConsultaId(solicitacao.getConsultaId() != null ? solicitacao.getConsultaId().getValue() : null);
        entity.setPrioridade(solicitacao.getPrioridade().name());
        entity.setObservacoes(solicitacao.getObservacoes());
        entity.setStatus(solicitacao.getStatus().name());
        entity.setDataCriacao(solicitacao.getDataCriacao());
        return entity;
    }

    private SolicitacaoExame toDomain(SolicitacaoExameEntity entity) {
        return new SolicitacaoExame(
                new SolicitacaoExameId(entity.getId()),
                new PacienteId(entity.getPacienteId()),
                new MedicoId(entity.getMedicoId()),
                new TipoExameId(entity.getTipoExameId()),
                entity.getAtendimentoId() != null ? new AtendimentoId(entity.getAtendimentoId()) : null,
                entity.getConsultaId() != null ? new ConsultaId(entity.getConsultaId()) : null,
                PrioridadeExame.valueOf(entity.getPrioridade()),
                entity.getObservacoes(),
                StatusSolicitacaoExame.valueOf(entity.getStatus()),
                entity.getDataCriacao()
        );
    }
}
