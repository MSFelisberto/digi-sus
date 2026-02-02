package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.exame.*;

import java.util.List;

public interface AgendamentoExameUseCase {
    AgendaExameOutput criarAgendaExame(CriarAgendaExameCommand command);
    List<VagaExameOutput> buscarVagasDisponiveis(BuscarVagasExameQuery query);
    AgendamentoExameOutput agendarExame(AgendarExameCommand command);
    void cancelarAgendamento(Long agendamentoId);
}