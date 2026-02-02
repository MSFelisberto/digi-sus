package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.exame.CriarTipoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.TipoExameOutput;
import br.com.dgs.agendamento.application.ports.inbound.TipoExameUseCase;
import br.com.dgs.agendamento.application.ports.outbound.TipoExameRepository;
import br.com.dgs.agendamento.domain.exception.ExameBusinessException;
import br.com.dgs.agendamento.domain.model.exame.TipoExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExameId;

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
                .orElseThrow(() -> new ExameBusinessException("Tipo de exame não encontrado com ID: " + id));
        return mapToOutput(tipoExame);
    }

    private TipoExameOutput mapToOutput(TipoExame t) {
        return new TipoExameOutput(t.getId().getValue(), t.getNome(), t.getCodigo(),
                t.getDescricao(), t.getPreparacao(), t.isAtivo());
    }
}