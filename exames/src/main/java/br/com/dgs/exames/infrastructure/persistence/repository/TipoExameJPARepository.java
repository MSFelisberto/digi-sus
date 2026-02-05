package br.com.dgs.exames.infrastructure.persistence.repository;

import br.com.dgs.exames.infrastructure.persistence.entity.TipoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoExameJPARepository extends JpaRepository<TipoExameEntity, Long> {
    Optional<TipoExameEntity> findByNomeIgnoreCase(String nome);
}
