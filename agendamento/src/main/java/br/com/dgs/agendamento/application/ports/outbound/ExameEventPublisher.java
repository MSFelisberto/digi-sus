package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.AgendamentoExame;

public interface ExameEventPublisher {
    void publicarExameAgendado(AgendamentoExame agendamento);
    void publicarExameCancelado(AgendamentoExame agendamento);
}
