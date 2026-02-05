package br.com.dgs.atendimento.domain.exception;

public class ConsultaInvalidaException extends RuntimeException {
    public ConsultaInvalidaException(String message) {
        super(message);
    }
}
