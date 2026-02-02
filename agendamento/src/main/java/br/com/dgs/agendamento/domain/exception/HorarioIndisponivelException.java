package br.com.dgs.agendamento.domain.exception;

public class HorarioIndisponivelException extends RuntimeException {
    public HorarioIndisponivelException(String message) {
        super(message);
    }

    public HorarioIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
