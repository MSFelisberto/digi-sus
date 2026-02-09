package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.CriarConsultaTriagemCommand;

public interface ConsultaTriagemUseCase {
    ConsultaOutput criarConsultaTriagem(CriarConsultaTriagemCommand command);
}
