package br.com.dgs.atendimento.domain.exception;

public class AtendimentoBusinessException extends RuntimeException {
    public AtendimentoBusinessException(String message) {
        super(message);
    }
}
