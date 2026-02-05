package br.com.dgs.atendimento.application.ports.outbound;

import br.com.dgs.atendimento.domain.model.Atendimento;

public interface AtendimentoEventPublisher {
    void publicarAtendimentoFinalizado(Atendimento atendimento);
    void publicarExameSolicitado(Long atendimentoId, Long consultaId, Long pacienteId, Long medicoId,
                                  String tipoExame, String prioridade, String observacoes);
}
