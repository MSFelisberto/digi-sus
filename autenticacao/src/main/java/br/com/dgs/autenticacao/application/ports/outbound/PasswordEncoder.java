package br.com.dgs.autenticacao.application.ports.outbound;

import br.com.dgs.autenticacao.domain.shared.model.Senha;

public interface PasswordEncoder {
    String encode(Senha senha);
    boolean matches(Senha rawPassword, String encodedPassword);
}
