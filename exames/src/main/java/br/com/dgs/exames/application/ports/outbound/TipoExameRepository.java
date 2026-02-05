package br.com.dgs.exames.application.ports.outbound;

import br.com.dgs.exames.domain.model.TipoExame;
import br.com.dgs.exames.domain.model.TipoExameId;

import java.util.List;
import java.util.Optional;

public interface TipoExameRepository {
    TipoExame save(TipoExame tipoExame);
    Optional<TipoExame> findById(TipoExameId id);
    Optional<TipoExame> findByNome(String nome);
    List<TipoExame> findAll();
}
