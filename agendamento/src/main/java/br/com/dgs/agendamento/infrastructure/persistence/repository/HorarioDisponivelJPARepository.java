package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.HorarioDisponivelEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioDisponivelJPARepository extends JpaRepository<HorarioDisponivelEntity, Long> {

    List<HorarioDisponivelEntity> findByEspecialidadeAndOcupadoFalseAndDataHoraBetween(
            String especialidade, LocalDateTime inicio, LocalDateTime fim);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HorarioDisponivelEntity h WHERE h.id = :id")
    Optional<HorarioDisponivelEntity> findByIdForUpdate(@Param("id") Long id);

    Optional<HorarioDisponivelEntity> findByConsultaId(Long consultaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HorarioDisponivelEntity h WHERE h.medicoId = :medicoId AND h.dataHora = :dataHora")
    Optional<HorarioDisponivelEntity> findByMedicoIdAndDataHoraForUpdate(
            @Param("medicoId") Long medicoId,
            @Param("dataHora") LocalDateTime dataHora);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HorarioDisponivelEntity h WHERE h.especialidade = :especialidade " +
           "AND h.ocupado = false AND h.dataHora BETWEEN :inicio AND :fim " +
           "ORDER BY h.dataHora ASC LIMIT 1")
    Optional<HorarioDisponivelEntity> findPrimeiroDisponivelPorEspecialidadeForUpdate(
            @Param("especialidade") String especialidade,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT DISTINCT h.medicoId FROM HorarioDisponivelEntity h " +
           "WHERE h.especialidade = :especialidade AND h.dataHora BETWEEN :inicio AND :fim " +
           "ORDER BY h.medicoId ASC LIMIT 1")
    Optional<Long> findPrimeiroMedicoIdPorEspecialidadeEData(
            @Param("especialidade") String especialidade,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}