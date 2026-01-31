package br.com.dgs.triagem.domain.exception;

public class TriagemBusinessException extends RuntimeException {
    public TriagemBusinessException(String message) {
        super(message);
    }
}
