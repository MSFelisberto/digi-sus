package br.com.dgs.autenticacao.domain.shared.exception;

public class SharedBusinessException extends RuntimeException {
    public SharedBusinessException(String message) {
        super(message);
    }

    public SharedBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
