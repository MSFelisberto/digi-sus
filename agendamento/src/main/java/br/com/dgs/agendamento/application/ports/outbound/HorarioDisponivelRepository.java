package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.ConsultaId;
import br.com.dgs.agendamento.domain.model.Especialidade;
import br.com.dgs.agendamento.domain.model.HorarioDisponivel;
import br.com.dgs.agendamento.domain.model.HorarioDisponivelId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioDisponivelRepository {
    HorarioDisponivel save(HorarioDisponivel horario);
    void saveAll(List<HorarioDisponivel> horarios);
    Optional<HorarioDisponivel> findById(HorarioDisponivelId id);
    Optional<HorarioDisponivel> findByIdParaReserva(HorarioDisponivelId id);
    List<HorarioDisponivel> findDisponiveisPorEspecialidadeEPeriodo(
            Especialidade especialidade, LocalDateTime inicio, LocalDateTime fim);
    Optional<HorarioDisponivel> findByConsultaId(ConsultaId consultaId);
}
