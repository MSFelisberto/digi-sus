package br.com.dgs.autenticacao.application.ports.inbound;

import br.com.dgs.autenticacao.application.dto.CadastrarPacienteCommand;
import br.com.dgs.autenticacao.application.dto.PacienteOutput;
import br.com.dgs.autenticacao.application.dto.ValidarPacienteQuery;

public interface PacienteUseCase {
    PacienteOutput cadastrarPaciente(CadastrarPacienteCommand command);
    boolean validarPacienteExiste(ValidarPacienteQuery query);
    PacienteOutput buscarPorId(Long id);
}
