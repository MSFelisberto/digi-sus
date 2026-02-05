package br.com.dgs.exames.application.ports.inbound;

import br.com.dgs.exames.application.dto.*;

import java.util.List;

public interface AgendamentoExameUseCase {
    AgendaExameOutput criarAgendaExame(CriarAgendaExameCommand command);
    List<VagaExameOutput> buscarVagasDisponiveis(BuscarVagasExameQuery query);
    AgendamentoExameOutput agendarExame(AgendarExameCommand command);
    void cancelarAgendamento(Long agendamentoId);
}
