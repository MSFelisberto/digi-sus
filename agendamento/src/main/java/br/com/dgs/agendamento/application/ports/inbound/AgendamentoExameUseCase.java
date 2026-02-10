package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.*;

import java.util.List;

public interface AgendamentoExameUseCase {
    AgendaExameOutput criarAgendaExame(CriarAgendaExameCommand command);
    void desativarAgendaExame(Long agendaExameId);
    void gerarHorariosExame(GerarHorariosExameCommand command);
    List<HorarioExameDisponivelOutput> buscarHorariosExameDisponiveis(BuscarHorariosExameQuery query);
    AgendamentoExameOutput agendarExame(AgendarExameCommand command);
    void cancelarAgendamentoExame(Long agendamentoId);
}
