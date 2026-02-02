package br.com.dgs.agendamento.domain.exception;

public class AgendaBusinessException extends RuntimeException {
    public AgendaBusinessException(String message) {
        super(message);
    }

    public AgendaBusinessException(String message, Throwable cause) {
        super(message, cause);
    } }
