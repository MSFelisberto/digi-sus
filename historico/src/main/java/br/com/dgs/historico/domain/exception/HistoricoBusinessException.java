package br.com.dgs.historico.domain.exception;

public class HistoricoBusinessException extends RuntimeException {
    public HistoricoBusinessException(String message) {
        super(message);
    }

    public HistoricoBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}