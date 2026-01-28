package br.com.dgs.historico.domain.exception;

public class HistoricoNotFoundException extends RuntimeException {
    public HistoricoNotFoundException(String message) {
        super(message);
    }

    public HistoricoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
