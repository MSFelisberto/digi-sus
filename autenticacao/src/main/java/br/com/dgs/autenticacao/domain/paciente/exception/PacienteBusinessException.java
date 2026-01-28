package br.com.dgs.autenticacao.domain.paciente.exception;

public class PacienteBusinessException extends RuntimeException {
    public PacienteBusinessException(String message) {
        super(message);
    }

    public PacienteBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
