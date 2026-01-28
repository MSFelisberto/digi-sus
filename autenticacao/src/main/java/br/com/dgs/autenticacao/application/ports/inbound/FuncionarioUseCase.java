package br.com.dgs.autenticacao.application.ports.inbound;

import br.com.dgs.autenticacao.application.dto.CadastrarFuncionarioCommand;
import br.com.dgs.autenticacao.application.dto.FuncionarioOutput;

public interface FuncionarioUseCase {
    FuncionarioOutput cadastrarFuncionario(CadastrarFuncionarioCommand command);
    FuncionarioOutput buscarPorId(Long id);
}
