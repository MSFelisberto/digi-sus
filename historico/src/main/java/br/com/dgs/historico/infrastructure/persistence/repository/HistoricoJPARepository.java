package br.com.dgs.historico.infrastructure.persistence.repository;

import br.com.dgs.historico.infrastructure.persistence.entity.HistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoricoJPARepository extends JpaRepository<HistoricoEntity, Long> {
    Optional<HistoricoEntity> findByConsultaId(Long consultaId);
    List<HistoricoEntity> findByPacienteId(Long pacienteId);
    boolean existsByConsultaId(Long consultaId);
}
