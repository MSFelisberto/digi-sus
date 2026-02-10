package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.HorarioExameDisponivel;
import br.com.dgs.agendamento.domain.model.HorarioExameDisponivelId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioExameDisponivelRepository {
    HorarioExameDisponivel save(HorarioExameDisponivel horario);
    void saveAll(List<HorarioExameDisponivel> horarios);
    Optional<HorarioExameDisponivel> findByIdParaReserva(HorarioExameDisponivelId id);
    List<HorarioExameDisponivel> findDisponiveisPorTipoExameEPeriodo(
            Long tipoExameId, LocalDateTime inicio, LocalDateTime fim);
}
