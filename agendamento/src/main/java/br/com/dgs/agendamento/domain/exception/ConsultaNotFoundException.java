package br.com.dgs.agendamento.domain.exception;

public class ConsultaNotFoundException extends RuntimeException {

    public ConsultaNotFoundException(String message) {
        super(message);
    }

    public ConsultaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}