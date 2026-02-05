package br.com.dgs.exames.domain.exception;

public class SolicitacaoExameNotFoundException extends RuntimeException {
    public SolicitacaoExameNotFoundException(String message) { super(message); }
    public SolicitacaoExameNotFoundException(Long id) {
        super("Solicitação de exame não encontrada com ID: " + id);
    }
}
