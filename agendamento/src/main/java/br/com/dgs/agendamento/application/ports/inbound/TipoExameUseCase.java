package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.exame.CriarTipoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.TipoExameOutput;

import java.util.List;

public interface TipoExameUseCase {
    TipoExameOutput criarTipoExame(CriarTipoExameCommand command);
    List<TipoExameOutput> listarTodos();
    TipoExameOutput buscarPorId(Long id);
}