package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendaExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaExameJPARepository extends JpaRepository<AgendaExameEntity, Long> {
    List<AgendaExameEntity> findByTipoExameIdAndAtivaTrue(Long tipoExameId);
}
