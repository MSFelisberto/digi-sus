package br.com.dgs.autenticacao.infrastructure.security.adapters;

import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;
import br.com.dgs.autenticacao.domain.shared.model.Senha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl implements PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encode(Senha senha) {
        return encoder.encode(senha.getValue());
    }

    @Override
    public boolean matches(Senha rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword.getValue(), encodedPassword);
    }
}
