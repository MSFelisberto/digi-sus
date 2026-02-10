package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.HorarioExameDisponivelEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioExameDisponivelJPARepository extends JpaRepository<HorarioExameDisponivelEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HorarioExameDisponivelEntity h WHERE h.id = :id")
    Optional<HorarioExameDisponivelEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT h FROM HorarioExameDisponivelEntity h WHERE h.tipoExameId = :tipoExameId " +
           "AND h.vagasOcupadas < h.vagasTotais AND h.dataHora BETWEEN :inicio AND :fim " +
           "ORDER BY h.dataHora ASC")
    List<HorarioExameDisponivelEntity> findDisponiveisPorTipoExameEPeriodo(
            @Param("tipoExameId") Long tipoExameId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
