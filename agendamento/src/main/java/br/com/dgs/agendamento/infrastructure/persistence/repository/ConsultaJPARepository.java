package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaJPARepository extends JpaRepository<ConsultaEntity, Long> {
    Optional<ConsultaEntity> findByTriagemId(Long triagemId);

    List<ConsultaEntity> findByPacienteId(Long pacienteId);

    List<ConsultaEntity> findByPacienteIdAndCanceladaFalseAndDataHoraAfter(
            Long pacienteId, LocalDateTime dataHora);

    List<ConsultaEntity> findByMedicoIdAndCanceladaFalseAndDataHoraAfter(
            Long medicoId, LocalDateTime dataHora);

    List<ConsultaEntity> findByCanceladaFalseAndDataHoraAfterOrderByDataHoraAsc(
            LocalDateTime dataHora);
}
