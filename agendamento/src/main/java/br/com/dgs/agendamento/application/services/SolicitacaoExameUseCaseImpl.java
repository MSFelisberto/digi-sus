package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.exame.CriarSolicitacaoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.ListarSolicitacoesQuery;
import br.com.dgs.agendamento.application.dto.exame.SolicitacaoExameOutput;
import br.com.dgs.agendamento.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ExameNotificationService;
import br.com.dgs.agendamento.application.ports.outbound.PacienteService;
import br.com.dgs.agendamento.application.ports.outbound.SolicitacaoExameRepository;
import br.com.dgs.agendamento.application.ports.outbound.TipoExameRepository;
import br.com.dgs.agendamento.domain.exception.AuthorizationException;
import br.com.dgs.agendamento.domain.exception.ExameBusinessException;
import br.com.dgs.agendamento.domain.exception.PacienteNotFoundException;
import br.com.dgs.agendamento.domain.exception.SolicitacaoExameNotFoundException;
import br.com.dgs.agendamento.domain.model.MedicoId;
import br.com.dgs.agendamento.domain.model.PacienteId;
import br.com.dgs.agendamento.domain.model.exame.*;

import java.util.List;
import java.util.stream.Collectors;

public class SolicitacaoExameUseCaseImpl implements SolicitacaoExameUseCase {

    private final SolicitacaoExameRepository solicitacaoRepository;
    private final TipoExameRepository tipoExameRepository;
    private final PacienteService pacienteService;
    private final ExameNotificationService exameNotificationService;

    public SolicitacaoExameUseCaseImpl(SolicitacaoExameRepository solicitacaoRepository,
                                       TipoExameRepository tipoExameRepository,
                                       PacienteService pacienteService,
                                       ExameNotificationService exameNotificationService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.tipoExameRepository = tipoExameRepository;
        this.pacienteService = pacienteService;
        this.exameNotificationService = exameNotificationService;
    }

    @Override
    public SolicitacaoExameOutput criarSolicitacao(CriarSolicitacaoExameCommand command) {
        PacienteId pacienteId = new PacienteId(command.pacienteId());
        if (!pacienteService.exists(pacienteId)) {
            throw new PacienteNotFoundException("Paciente não encontrado com ID: " + command.pacienteId());
        }

        TipoExameId tipoExameId = new TipoExameId(command.tipoExameId());
        TipoExame tipoExame = tipoExameRepository.findById(tipoExameId)
                .orElseThrow(() -> new ExameBusinessException("Tipo de exame não encontrado com ID: " + command.tipoExameId()));

        PrioridadeExame prioridade = PrioridadeExame.valueOf(command.prioridade().toUpperCase());

        SolicitacaoExame solicitacao = new SolicitacaoExame(
                pacienteId,
                new MedicoId(command.medicoId()),
                tipoExameId,
                prioridade,
                command.observacoes()
        );

        SolicitacaoExame salva = solicitacaoRepository.save(solicitacao);

        exameNotificationService.notificarSolicitacao(salva, tipoExame);

        return mapToOutput(salva, tipoExame.getNome());
    }

    @Override
    public List<SolicitacaoExameOutput> listarPorPaciente(ListarSolicitacoesQuery query) {
        PacienteId pacienteId = new PacienteId(query.pacienteId());

        if (query.currentUser().hasRole("PACIENTE")) {
            PacienteId currentUserId = new PacienteId(query.currentUser().getId());
            if (!pacienteId.equals(currentUserId)) {
                throw new AuthorizationException("Paciente só pode visualizar suas próprias solicitações");
            }
        }

        return solicitacaoRepository.findByPacienteId(pacienteId).stream()
                .map(s -> {
                    String nomeExame = tipoExameRepository.findById(s.getTipoExameId())
                            .map(TipoExame::getNome).orElse("DESCONHECIDO");
                    return mapToOutput(s, nomeExame);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void cancelarSolicitacao(Long solicitacaoId) {
        SolicitacaoExameId id = new SolicitacaoExameId(solicitacaoId);
        SolicitacaoExame solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new SolicitacaoExameNotFoundException(
                        "Solicitação não encontrada com ID: " + solicitacaoId));

        solicitacao.cancelar();
        solicitacaoRepository.save(solicitacao);
    }

    private SolicitacaoExameOutput mapToOutput(SolicitacaoExame s, String tipoExameNome) {
        return new SolicitacaoExameOutput(
                s.getId().getValue(), s.getPacienteId().getValue(), s.getMedicoId().getValue(),
                s.getTipoExameId().getValue(), tipoExameNome, s.getPrioridade().name(),
                s.getObservacoes(), s.getStatus().name(), s.getDataCriacao()
        );
    }
}