package br.com.dgs.exames.application.services;

import br.com.dgs.exames.application.dto.*;
import br.com.dgs.exames.application.ports.inbound.AgendamentoExameUseCase;
import br.com.dgs.exames.application.ports.outbound.*;
import br.com.dgs.exames.domain.exception.AgendamentoExameNotFoundException;
import br.com.dgs.exames.domain.exception.ExameBusinessException;
import br.com.dgs.exames.domain.exception.SolicitacaoExameNotFoundException;
import br.com.dgs.exames.domain.exception.VagaExameIndisponivelException;
import br.com.dgs.exames.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoExameUseCaseImpl implements AgendamentoExameUseCase {

    private final AgendamentoExameRepository agendamentoRepository;
    private final SolicitacaoExameRepository solicitacaoRepository;
    private final AgendaExameRepository agendaExameRepository;
    private final TipoExameRepository tipoExameRepository;
    private final ExameNotificationService exameNotificationService;

    public AgendamentoExameUseCaseImpl(AgendamentoExameRepository agendamentoRepository,
                                       SolicitacaoExameRepository solicitacaoRepository,
                                       AgendaExameRepository agendaExameRepository,
                                       TipoExameRepository tipoExameRepository,
                                       ExameNotificationService exameNotificationService) {
        this.agendamentoRepository = agendamentoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.agendaExameRepository = agendaExameRepository;
        this.tipoExameRepository = tipoExameRepository;
        this.exameNotificationService = exameNotificationService;
    }

    @Override
    public AgendaExameOutput criarAgendaExame(CriarAgendaExameCommand command) {
        TipoExameId tipoExameId = new TipoExameId(command.tipoExameId());
        tipoExameRepository.findById(tipoExameId)
                .orElseThrow(() -> new ExameBusinessException(
                        "Tipo de exame não encontrado com ID: " + command.tipoExameId()));

        AgendaExame agenda = new AgendaExame(
                tipoExameId, command.diaSemana(), command.horaInicio(),
                command.horaFim(), command.duracaoSlotMinutos(), command.vagasPorSlot()
        );

        AgendaExame salva = agendaExameRepository.save(agenda);
        return mapToAgendaOutput(salva);
    }

    @Override
    public List<VagaExameOutput> buscarVagasDisponiveis(BuscarVagasExameQuery query) {
        TipoExameId tipoExameId = new TipoExameId(query.tipoExameId());
        List<AgendaExame> agendas = agendaExameRepository.findByTipoExameIdAndAtivaTrue(tipoExameId);

        List<VagaExameOutput> vagas = new ArrayList<>();

        LocalDate dataAtual = query.dataInicio();
        while (!dataAtual.isAfter(query.dataFim())) {
            for (AgendaExame agenda : agendas) {
                List<LocalDateTime> slots = agenda.gerarSlots(dataAtual);
                for (LocalDateTime slot : slots) {
                    long ocupadas = agendamentoRepository.countByDataHoraAndTipoExameIdAndStatusNot(
                            slot, tipoExameId, StatusAgendamentoExame.CANCELADO.name());
                    int restantes = agenda.getVagasPorSlot() - (int) ocupadas;
                    if (restantes > 0) {
                        vagas.add(new VagaExameOutput(slot, restantes, tipoExameId.getValue()));
                    }
                }
            }
            dataAtual = dataAtual.plusDays(1);
        }

        return vagas;
    }

    @Override
    public AgendamentoExameOutput agendarExame(AgendarExameCommand command) {
        SolicitacaoExameId solicitacaoId = new SolicitacaoExameId(command.solicitacaoExameId());
        SolicitacaoExame solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoExameNotFoundException(command.solicitacaoExameId()));

        TipoExame tipoExame = tipoExameRepository.findById(solicitacao.getTipoExameId())
                .orElseThrow(() -> new ExameBusinessException("Tipo de exame não encontrado"));

        TipoExameId tipoExameId = solicitacao.getTipoExameId();
        List<AgendaExame> agendas = agendaExameRepository.findByTipoExameIdAndAtivaTrue(tipoExameId);

        boolean slotValido = agendas.stream()
                .anyMatch(a -> a.gerarSlots(command.dataHora().toLocalDate())
                        .contains(command.dataHora()));

        if (!slotValido) {
            throw new VagaExameIndisponivelException("Horário não disponível para este tipo de exame");
        }

        long ocupadas = agendamentoRepository.countByDataHoraAndTipoExameIdAndStatusNot(
                command.dataHora(), tipoExameId, StatusAgendamentoExame.CANCELADO.name());
        int vagasPorSlot = agendas.stream()
                .filter(a -> !a.gerarSlots(command.dataHora().toLocalDate()).isEmpty())
                .findFirst()
                .map(AgendaExame::getVagasPorSlot)
                .orElse(0);

        if (ocupadas >= vagasPorSlot) {
            throw new VagaExameIndisponivelException("Não há vagas disponíveis neste horário");
        }

        AgendamentoExame agendamento = new AgendamentoExame(solicitacaoId, tipoExameId, command.dataHora());
        solicitacao.agendar();

        AgendamentoExame salvo = agendamentoRepository.save(agendamento);
        solicitacaoRepository.save(solicitacao);

        exameNotificationService.notificarAgendamento(solicitacao, tipoExame, command.dataHora());

        return mapToAgendamentoOutput(salvo);
    }

    @Override
    public void cancelarAgendamento(Long agendamentoId) {
        AgendamentoExameId id = new AgendamentoExameId(agendamentoId);
        AgendamentoExame agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new AgendamentoExameNotFoundException(agendamentoId));

        agendamento.cancelar();
        agendamentoRepository.save(agendamento);

        SolicitacaoExame solicitacao = solicitacaoRepository.findById(agendamento.getSolicitacaoExameId())
                .orElseThrow(() -> new SolicitacaoExameNotFoundException("Solicitação não encontrada"));

        solicitacao.retornarParaPendente();
        solicitacaoRepository.save(solicitacao);

        TipoExame tipoExame = tipoExameRepository.findById(agendamento.getTipoExameId()).orElse(null);
        if (tipoExame != null) {
            exameNotificationService.notificarCancelamento(solicitacao, tipoExame);
        }
    }

    private AgendaExameOutput mapToAgendaOutput(AgendaExame a) {
        return new AgendaExameOutput(
                a.getId().getValue(), a.getTipoExameId().getValue(), a.getDiaSemana().name(),
                a.getHoraInicio().toString(), a.getHoraFim().toString(),
                a.getDuracaoSlotMinutos(), a.getVagasPorSlot(), a.isAtiva()
        );
    }

    private AgendamentoExameOutput mapToAgendamentoOutput(AgendamentoExame a) {
        return new AgendamentoExameOutput(
                a.getId().getValue(), a.getSolicitacaoExameId().getValue(),
                a.getTipoExameId().getValue(), a.getDataHora(), a.getStatus().name(), a.getDataCriacao()
        );
    }
}
