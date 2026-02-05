package br.com.dgs.atendimento.infrastructure.persistence.repository;

import br.com.dgs.atendimento.infrastructure.persistence.entity.AtendimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtendimentoJPARepository extends JpaRepository<AtendimentoEntity, Long> {
    Optional<AtendimentoEntity> findByConsultaId(Long consultaId);
    boolean existsByConsultaId(Long consultaId);
}
