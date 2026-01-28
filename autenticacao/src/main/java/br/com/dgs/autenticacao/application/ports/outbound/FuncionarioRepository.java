package br.com.dgs.autenticacao.application.ports.outbound;

import java.util.Optional;

public interface FuncionarioRepository {
    Funcionario save(Funcionario funcionario);
    Optional<Funcionario> findByEmail(Email email);
    Optional<Funcionario> findById(FuncionarioId id);
    boolean existsByEmail(Email email);
    boolean existsByCpf(String cpf);
    boolean existsByCrm(String crm);
    boolean existsByCoren(String coren);
}
