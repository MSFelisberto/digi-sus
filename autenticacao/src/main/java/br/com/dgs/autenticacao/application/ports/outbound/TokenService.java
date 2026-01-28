package br.com.dgs.autenticacao.application.ports.outbound;

import br.com.dgs.autenticacao.domain.funcionario.model.Funcionario;
import br.com.dgs.autenticacao.domain.paciente.model.Paciente;

public interface TokenService {
    String generateTokenForPaciente(Paciente paciente);
    String generateTokenForFuncionario(Funcionario funcionario);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    Long getUserIdFromToken(String token);
    String getUserTypeFromToken(String token);
}