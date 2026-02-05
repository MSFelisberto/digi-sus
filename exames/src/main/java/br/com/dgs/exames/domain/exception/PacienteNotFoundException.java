package br.com.dgs.exames.domain.exception;

public class PacienteNotFoundException extends RuntimeException {
    public PacienteNotFoundException(String message) { super(message); }
    public PacienteNotFoundException(Long id) {
        super("Paciente não encontrado com ID: " + id);
    }
}
