package br.com.dgs.exames.application.ports.inbound;

import br.com.dgs.exames.application.dto.CriarTipoExameCommand;
import br.com.dgs.exames.application.dto.TipoExameOutput;

import java.util.List;

public interface TipoExameUseCase {
    TipoExameOutput criarTipoExame(CriarTipoExameCommand command);
    List<TipoExameOutput> listarTodos();
    TipoExameOutput buscarPorId(Long id);
}
