package br.com.dgs.agendamento.domain.exception;

public class VagaExameIndisponivelException extends RuntimeException {
    public VagaExameIndisponivelException(String message) { super(message); }
}