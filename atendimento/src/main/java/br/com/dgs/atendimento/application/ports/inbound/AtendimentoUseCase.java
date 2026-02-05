package br.com.dgs.atendimento.application.ports.inbound;

import br.com.dgs.atendimento.application.dto.*;

public interface AtendimentoUseCase {
    AtendimentoOutput iniciarAtendimento(IniciarAtendimentoCommand command);
    AtendimentoOutput finalizarAtendimento(FinalizarAtendimentoCommand command);
    void solicitarExame(SolicitarExameCommand command);
    AtendimentoOutput buscarPorId(Long atendimentoId);
    AtendimentoOutput buscarPorConsultaId(Long consultaId);
}
