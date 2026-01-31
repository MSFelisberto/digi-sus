package br.com.dgs.historico.infrastructure.persistence.repository;

import br.com.dgs.historico.infrastructure.persistence.entity.TriagemHistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TriagemHistoricoJPARepository extends JpaRepository<TriagemHistoricoEntity, Long> {
    Optional<TriagemHistoricoEntity> findByTriagemId(Long triagemId);
    List<TriagemHistoricoEntity> findByPacienteId(Long pacienteId);
    boolean existsByTriagemId(Long triagemId);
}
