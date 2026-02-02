package br.com.dgs.agendamento.domain.exception;

public class ExameBusinessException extends RuntimeException {
    public ExameBusinessException(String message) { super(message); }
    public ExameBusinessException(String message, Throwable cause) { super(message, cause); }
}