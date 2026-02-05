package br.com.dgs.exames.application.services;

import br.com.dgs.exames.application.dto.CriarTipoExameCommand;
import br.com.dgs.exames.application.dto.TipoExameOutput;
import br.com.dgs.exames.application.ports.inbound.TipoExameUseCase;
import br.com.dgs.exames.application.ports.outbound.TipoExameRepository;
import br.com.dgs.exames.domain.exception.TipoExameNotFoundException;
import br.com.dgs.exames.domain.model.TipoExame;
import br.com.dgs.exames.domain.model.TipoExameId;

import java.util.List;
import java.util.stream.Collectors;

public class TipoExameUseCaseImpl implements TipoExameUseCase {

    private final TipoExameRepository tipoExameRepository;

    public TipoExameUseCaseImpl(TipoExameRepository tipoExameRepository) {
        this.tipoExameRepository = tipoExameRepository;
    }

    @Override
    public TipoExameOutput criarTipoExame(CriarTipoExameCommand command) {
        TipoExame tipoExame = new TipoExame(
                command.nome(), command.codigo(), command.descricao(), command.preparacao()
        );
        TipoExame salvo = tipoExameRepository.save(tipoExame);
        return mapToOutput(salvo);
    }

    @Override
    public List<TipoExameOutput> listarTodos() {
        return tipoExameRepository.findAll().stream()
                .map(this::mapToOutput)
                .collect(Collectors.toList());
    }

    @Override
    public TipoExameOutput buscarPorId(Long id) {
        TipoExameId tipoExameId = new TipoExameId(id);
        TipoExame tipoExame = tipoExameRepository.findById(tipoExameId)
                .orElseThrow(() -> new TipoExameNotFoundException(id));
        return mapToOutput(tipoExame);
    }

    private TipoExameOutput mapToOutput(TipoExame t) {
        return new TipoExameOutput(t.getId().getValue(), t.getNome(), t.getCodigo(),
                t.getDescricao(), t.getPreparacao(), t.isAtivo());
    }
}
