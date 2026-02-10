package br.com.dgs.agendamento.infrastructure.persistence.repository;

import br.com.dgs.agendamento.infrastructure.persistence.entity.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaJPARepository extends JpaRepository<ConsultaEntity, Long> {
    Optional<ConsultaEntity> findByTriagemId(Long triagemId);

    List<ConsultaEntity> findByPacienteId(Long pacienteId);

    List<ConsultaEntity> findByPacienteIdAndStatusNotAndDataHoraAfter(
            Long pacienteId, String status, LocalDateTime dataHora);

    List<ConsultaEntity> findByMedicoIdAndStatusNotAndDataHoraAfter(
            Long medicoId, String status, LocalDateTime dataHora);

    List<ConsultaEntity> findByStatusNotAndDataHoraAfterOrderByDataHoraAsc(
            String status, LocalDateTime dataHora);
}
