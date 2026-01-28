package br.com.dgs.autenticacao.domain.funcionario.exception;

public class FuncionarioBusinessException extends RuntimeException {
  public FuncionarioBusinessException(String message) {
    super(message);
  }

  public FuncionarioBusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
