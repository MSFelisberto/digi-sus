package br.com.dgs.triagem.application.ports.inbound;

import br.com.dgs.triagem.application.dto.AuthenticatedUser;
import br.com.dgs.triagem.application.dto.RealizarTriagemCommand;
import br.com.dgs.triagem.application.dto.TriagemOutput;

public interface TriagemUseCase {
    TriagemOutput realizarTriagem(RealizarTriagemCommand command, AuthenticatedUser user);
}
