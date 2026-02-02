package br.com.dgs.agendamento.infrastructure.persistence.repository.exame;

import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.AgendamentoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AgendamentoExameJPARepository extends JpaRepository<AgendamentoExameEntity, Long> {
    long countByDataHoraAndTipoExameIdAndStatusNot(LocalDateTime dataHora, Long tipoExameId, String statusExcluido);
}