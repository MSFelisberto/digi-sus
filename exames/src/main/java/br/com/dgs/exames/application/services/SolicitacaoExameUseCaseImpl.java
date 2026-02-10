package br.com.dgs.exames.application.services;

import br.com.dgs.exames.application.dto.CriarSolicitacaoExameCommand;
import br.com.dgs.exames.application.dto.CriarSolicitacaoExamePorNomeCommand;
import br.com.dgs.exames.application.dto.ListarSolicitacoesQuery;
import br.com.dgs.exames.application.dto.SolicitacaoExameOutput;
import br.com.dgs.exames.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.exames.application.ports.outbound.ExameNotificationService;
import br.com.dgs.exames.application.ports.outbound.PacienteService;
import br.com.dgs.exames.application.ports.outbound.SolicitacaoExameRepository;
import br.com.dgs.exames.application.ports.outbound.TipoExameRepository;
import br.com.dgs.exames.domain.exception.AuthorizationException;
import br.com.dgs.exames.domain.exception.ExameBusinessException;
import br.com.dgs.exames.domain.exception.PacienteNotFoundException;
import br.com.dgs.exames.domain.exception.SolicitacaoExameNotFoundException;
import br.com.dgs.exames.domain.model.*;

import java.time.LocalDateTime;
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
            throw new PacienteNotFoundException(command.pacienteId());
        }

        TipoExameId tipoExameId = new TipoExameId(command.tipoExameId());
        TipoExame tipoExame = tipoExameRepository.findById(tipoExameId)
                .orElseThrow(() -> new ExameBusinessException("Tipo de exame não encontrado com ID: " + command.tipoExameId()));

        PrioridadeExame prioridade = PrioridadeExame.valueOf(command.prioridade().toUpperCase());

        AtendimentoId atendimentoId = command.atendimentoId() != null ? new AtendimentoId(command.atendimentoId()) : null;
        ConsultaId consultaId = command.consultaId() != null ? new ConsultaId(command.consultaId()) : null;

        SolicitacaoExame solicitacao = new SolicitacaoExame(
                pacienteId,
                new MedicoId(command.medicoId()),
                tipoExameId,
                atendimentoId,
                consultaId,
                prioridade,
                command.observacoes()
        );

        SolicitacaoExame salva = solicitacaoRepository.save(solicitacao);

        exameNotificationService.notificarSolicitacao(salva, tipoExame);

        return mapToOutput(salva, tipoExame.getNome());
    }

    @Override
    public SolicitacaoExameOutput criarSolicitacaoPorNomeExame(CriarSolicitacaoExamePorNomeCommand command) {
        PacienteId pacienteId = new PacienteId(command.pacienteId());
        if (!pacienteService.exists(pacienteId)) {
            throw new PacienteNotFoundException(command.pacienteId());
        }

        TipoExame tipoExame = tipoExameRepository.findByNome(command.tipoExameNome().toUpperCase())
                .orElseThrow(() -> new ExameBusinessException("Tipo de exame não encontrado: " + command.tipoExameNome()));

        PrioridadeExame prioridade = PrioridadeExame.valueOf(command.prioridade().toUpperCase());

        AtendimentoId atendimentoId = command.atendimentoId() != null ? new AtendimentoId(command.atendimentoId()) : null;
        ConsultaId consultaId = command.consultaId() != null ? new ConsultaId(command.consultaId()) : null;

        SolicitacaoExame solicitacao = new SolicitacaoExame(
                pacienteId,
                new MedicoId(command.medicoId()),
                tipoExame.getId(),
                atendimentoId,
                consultaId,
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
    public List<SolicitacaoExameOutput> listarPorAtendimento(Long atendimentoId) {
        AtendimentoId id = new AtendimentoId(atendimentoId);
        return solicitacaoRepository.findByAtendimentoId(id).stream()
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
                .orElseThrow(() -> new SolicitacaoExameNotFoundException(solicitacaoId));

        TipoExame tipoExame = tipoExameRepository.findById(solicitacao.getTipoExameId())
                .orElse(null);

        solicitacao.cancelar();
        solicitacaoRepository.save(solicitacao);

        if (tipoExame != null) {
            exameNotificationService.notificarCancelamento(solicitacao, tipoExame);
        }
    }

    @Override
    public void marcarComoAgendada(Long solicitacaoId, LocalDateTime dataHora) {
        SolicitacaoExameId id = new SolicitacaoExameId(solicitacaoId);
        SolicitacaoExame solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new SolicitacaoExameNotFoundException(solicitacaoId));

        solicitacao.agendar();
        solicitacaoRepository.save(solicitacao);

        TipoExame tipoExame = tipoExameRepository.findById(solicitacao.getTipoExameId()).orElse(null);
        if (tipoExame != null) {
            exameNotificationService.notificarAgendamento(solicitacao, tipoExame, dataHora);
        }
    }

    @Override
    public void retornarParaPendente(Long solicitacaoId) {
        SolicitacaoExameId id = new SolicitacaoExameId(solicitacaoId);
        SolicitacaoExame solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new SolicitacaoExameNotFoundException(solicitacaoId));

        solicitacao.retornarParaPendente();
        solicitacaoRepository.save(solicitacao);

        TipoExame tipoExame = tipoExameRepository.findById(solicitacao.getTipoExameId()).orElse(null);
        if (tipoExame != null) {
            exameNotificationService.notificarCancelamento(solicitacao, tipoExame);
        }
    }

    private SolicitacaoExameOutput mapToOutput(SolicitacaoExame s, String tipoExameNome) {
        return new SolicitacaoExameOutput(
                s.getId().getValue(),
                s.getPacienteId().getValue(),
                s.getMedicoId().getValue(),
                s.getTipoExameId().getValue(),
                tipoExameNome,
                s.getAtendimentoId() != null ? s.getAtendimentoId().getValue() : null,
                s.getConsultaId() != null ? s.getConsultaId().getValue() : null,
                s.getPrioridade().name(),
                s.getObservacoes(),
                s.getStatus().name(),
                s.getDataCriacao()
        );
    }
}
