package br.com.dgs.exames.infrastructure.persistence.repository;

import br.com.dgs.exames.infrastructure.persistence.entity.SolicitacaoExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoExameJPARepository extends JpaRepository<SolicitacaoExameEntity, Long> {
    List<SolicitacaoExameEntity> findByPacienteId(Long pacienteId);
    List<SolicitacaoExameEntity> findByAtendimentoId(Long atendimentoId);
}
