package br.com.dgs.autenticacao.application.ports.outbound;

import java.util.Optional;

public interface PacienteRepository {
    Paciente save(Paciente paciente);
    Optional<Paciente> findByEmail(Email email);
    Optional<Paciente> findById(PacienteId id);
    boolean existsByEmail(Email email);
    boolean existsByCpf(String cpf);
}
