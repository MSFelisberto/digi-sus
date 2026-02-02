package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.AutoAgendarCommand;
import br.com.dgs.agendamento.application.dto.BuscarHorariosQuery;
import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.HorarioDisponivelOutput;

import java.util.List;

public interface HorarioDisponivelUseCase {
    List<HorarioDisponivelOutput> buscarHorariosDisponiveis(BuscarHorariosQuery query);
    ConsultaOutput autoAgendar(AutoAgendarCommand command);
}