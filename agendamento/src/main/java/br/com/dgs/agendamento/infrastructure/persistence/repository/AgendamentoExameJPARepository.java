package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendamentoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoExameJPARepository extends JpaRepository<AgendamentoExameEntity, Long> {
}
