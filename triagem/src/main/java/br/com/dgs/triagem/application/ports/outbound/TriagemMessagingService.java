package br.com.dgs.triagem.application.ports.outbound;

import br.com.dgs.triagem.domain.model.Triagem;

public interface TriagemMessagingService {
    void enviarParaAtendimento(Triagem triagem);
    void enviarParaHistorico(Triagem triagem);
}
