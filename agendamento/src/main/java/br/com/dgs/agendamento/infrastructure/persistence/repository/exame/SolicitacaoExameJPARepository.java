package br.com.dgs.agendamento.infrastructure.persistence.repository.exame;

import br.com.dgs.agendamento.infrastructure.persistence.entity.exame.SolicitacaoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoExameJPARepository extends JpaRepository<SolicitacaoExameEntity, Long> {
    List<SolicitacaoExameEntity> findByPacienteId(Long pacienteId);
}