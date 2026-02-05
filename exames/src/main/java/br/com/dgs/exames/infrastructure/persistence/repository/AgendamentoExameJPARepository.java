package br.com.dgs.exames.infrastructure.persistence.repository;

import br.com.dgs.exames.infrastructure.persistence.entity.AgendamentoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AgendamentoExameJPARepository extends JpaRepository<AgendamentoExameEntity, Long> {
    long countByDataHoraAndTipoExameIdAndStatusNot(LocalDateTime dataHora, Long tipoExameId, String statusExcluido);
}
