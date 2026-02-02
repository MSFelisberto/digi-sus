package br.com.dgs.agendamento.infrastructure.persistence.repository.exame;

import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.TipoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoExameJPARepository extends JpaRepository<TipoExameEntity, Long> {
}