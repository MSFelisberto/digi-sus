package br.com.dgs.agendamento.infrastructure.persistence.adapters.exame;

import br.com.dgs.agendamento.application.ports.outbound.TipoExameRepository;
import br.com.dgs.agendamento.domain.model.exame.TipoExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExameId;
import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.TipoExameEntity;
import br.com.dgs.agendamento.infrastructure.persistence.repository.exame.TipoExameJPARepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TipoExameRepositoryImpl implements TipoExameRepository {

    private final TipoExameJPARepository jpaRepository;

    public TipoExameRepositoryImpl(TipoExameJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TipoExame save(TipoExame tipoExame) {
        TipoExameEntity entity = toEntity(tipoExame);
        TipoExameEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TipoExame> findById(TipoExameId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<TipoExame> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TipoExameEntity toEntity(TipoExame tipoExame) {
        TipoExameEntity entity = new TipoExameEntity();
        if (tipoExame.getId() != null) {
            entity.setId(tipoExame.getId().getValue());
        }
        entity.setNome(tipoExame.getNome());
        entity.setCodigo(tipoExame.getCodigo());
        entity.setDescricao(tipoExame.getDescricao());
        entity.setPreparacao(tipoExame.getPreparacao());
        entity.setAtivo(tipoExame.isAtivo());
        return entity;
    }

    private TipoExame toDomain(TipoExameEntity entity) {
        return new TipoExame(
                new TipoExameId(entity.getId()),
                entity.getNome(),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getPreparacao(),
                entity.isAtivo()
        );
    }
}