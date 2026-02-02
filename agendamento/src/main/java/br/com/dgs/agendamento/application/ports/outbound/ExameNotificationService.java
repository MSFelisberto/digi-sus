package br.com.dgs.agendamento.application.ports.outbound;

import br.com.dgs.agendamento.domain.model.exame.SolicitacaoExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExame;

import java.time.LocalDateTime;

public interface ExameNotificationService {
    void notificarSolicitacao(SolicitacaoExame solicitacao, TipoExame tipoExame);
    void notificarAgendamento(SolicitacaoExame solicitacao, TipoExame tipoExame, LocalDateTime dataHora);
    void notificarCancelamento(SolicitacaoExame solicitacao, TipoExame tipoExame);
}