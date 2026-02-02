package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.AgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaJPARepository extends JpaRepository<AgendaEntity, Long> {
    List<AgendaEntity> findByMedicoId(Long medicoId);
}