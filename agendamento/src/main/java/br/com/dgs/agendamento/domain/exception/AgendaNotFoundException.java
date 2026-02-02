package br.com.dgs.agendamento.domain.exception;

public class AgendaNotFoundException extends RuntimeException {
    public AgendaNotFoundException(String message) {
        super(message);
    }

    public AgendaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
