package br.com.dgs.autenticacao.application.ports.inbound;

import br.com.dgs.autenticacao.application.dto.AutenticarCommand;
import br.com.dgs.autenticacao.application.dto.AuthTokenOutput;

public interface AutenticacaoUseCase {
    AuthTokenOutput autenticar(AutenticarCommand command);
}
