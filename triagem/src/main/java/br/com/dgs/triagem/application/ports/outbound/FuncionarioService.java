package br.com.dgs.triagem.application.ports.outbound;

public interface FuncionarioService {
    boolean existeFuncionario(Long funcionarioId);
    boolean isEnfermeiro(Long funcionarioId);
}
