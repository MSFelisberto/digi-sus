package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.SolicitacaoExame;
import br.com.dgs.exames.domain.model.TipoExame;

import java.time.LocalDateTime;

public interface ExameNotificationService {
    void notificarSolicitacao(SolicitacaoExame solicitacao, TipoExame tipoExame);
    void notificarAgendamento(SolicitacaoExame solicitacao, TipoExame tipoExame, LocalDateTime dataHora);
    void notificarCancelamento(SolicitacaoExame solicitacao, TipoExame tipoExame);
}
