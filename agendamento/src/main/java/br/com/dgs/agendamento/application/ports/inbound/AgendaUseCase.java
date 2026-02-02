package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.AgendaOutput;
import br.com.dgs.agendamento.application.dto.CriarAgendaCommand;
import br.com.dgs.agendamento.application.dto.GerarHorariosCommand;

import java.util.List;

public interface AgendaUseCase {
    AgendaOutput criarAgenda(CriarAgendaCommand command);
    void desativarAgenda(Long agendaId);
    List<AgendaOutput> listarAgendasPorMedico(Long medicoId);
    void gerarHorarios(GerarHorariosCommand command);
}
