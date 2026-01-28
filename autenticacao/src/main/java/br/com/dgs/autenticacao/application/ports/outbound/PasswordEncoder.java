package br.com.dgs.autenticacao.application.ports.outbound;

public interface PasswordEncoder {
    String encode(Senha senha);
    boolean matches(Senha rawPassword, String encodedPassword);
}
