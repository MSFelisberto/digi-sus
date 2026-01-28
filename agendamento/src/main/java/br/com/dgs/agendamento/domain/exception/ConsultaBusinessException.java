package br.com.dgs.agendamento.domain.exception;

public class ConsultaBusinessException extends RuntimeException {
  public ConsultaBusinessException(String message) {
    super(message);
  }

  public ConsultaBusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
