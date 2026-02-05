package br.com.dgs.exames.domain.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) { super(message); }
}
