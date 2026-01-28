package br.com.dgs.autenticacao.application.ports.outbound;

public interface TokenService {
    String generateTokenForPaciente(Paciente paciente);
    String generateTokenForFuncionario(Funcionario funcionario);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    Long getUserIdFromToken(String token);
    String getUserTypeFromToken(String token);
}