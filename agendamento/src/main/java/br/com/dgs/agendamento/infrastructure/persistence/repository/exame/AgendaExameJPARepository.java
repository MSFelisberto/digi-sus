package br.com.dgs.agendamento.infrastructure.persistence.repository.exame;

import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.AgendaExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaExameJPARepository extends JpaRepository<AgendaExameEntity, Long> {
    List<AgendaExameEntity> findByTipoExameIdAndAtivaTrue(Long tipoExameId);
}