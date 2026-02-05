package br.com.dgs.exames.domain.exception;

public class AgendamentoExameNotFoundException extends RuntimeException {
    public AgendamentoExameNotFoundException(String message) { super(message); }
    public AgendamentoExameNotFoundException(Long id) {
        super("Agendamento de exame não encontrado com ID: " + id);
    }
}
