package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.Consulta;

public interface NotificationService {
    void notificarAgendamento(Consulta consulta);
    void notificarCancelamento(Consulta consulta);
    void notificarReagendamento(Consulta consulta);
}
