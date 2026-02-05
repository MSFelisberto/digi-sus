package br.com.dgs.atendimento.domain.exception;

public class AtendimentoNotFoundException extends RuntimeException {
    public AtendimentoNotFoundException(String message) {
        super(message);
    }
}
