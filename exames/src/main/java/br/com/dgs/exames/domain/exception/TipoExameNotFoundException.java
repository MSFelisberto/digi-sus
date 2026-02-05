package br.com.dgs.exames.domain.exception;

public class TipoExameNotFoundException extends RuntimeException {
    public TipoExameNotFoundException(String message) { super(message); }
    public TipoExameNotFoundException(Long id) {
        super("Tipo de exame não encontrado com ID: " + id);
    }
}
