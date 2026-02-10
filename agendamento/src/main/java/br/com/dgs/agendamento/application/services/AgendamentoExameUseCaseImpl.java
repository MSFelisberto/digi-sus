package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.*;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoExameUseCase;
import br.com.dgs.agendamento.application.ports.outbound.AgendaExameRepository;
import br.com.dgs.agendamento.application.ports.outbound.AgendamentoExameRepository;
import br.com.dgs.agendamento.application.ports.outbound.ExameEventPublisher;
import br.com.dgs.agendamento.application.ports.outbound.HorarioExameDisponivelRepository;
import br.com.dgs.agendamento.domain.exception.AgendaNotFoundException;
import br.com.dgs.agendamento.domain.exception.ConsultaBusinessException;
import br.com.dgs.agendamento.domain.exception.ConsultaNotFoundException;
import br.com.dgs.agendamento.domain.exception.HorarioIndisponivelException;
import br.com.dgs.agendamento.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AgendamentoExameUseCaseImpl implements AgendamentoExameUseCase {

    private final AgendaExameRepository agendaExameRepository;
    private final HorarioExameDisponivelRepository horarioExameRepository;
    private final AgendamentoExameRepository agendamentoExameRepository;
    private final ExameEventPublisher exameEventPublisher;

    public AgendamentoExameUseCaseImpl(AgendaExameRepository agendaExameRepository,
                                       HorarioExameDisponivelRepository horarioExameRepository,
                                       AgendamentoExameRepository agendamentoExameRepository,
                                       ExameEventPublisher exameEventPublisher) {
        this.agendaExameRepository = agendaExameRepository;
        this.horarioExameRepository = horarioExameRepository;
        this.agendamentoExameRepository = agendamentoExameRepository;
        this.exameEventPublisher = exameEventPublisher;
    }

    @Override
    public AgendaExameOutput criarAgendaExame(CriarAgendaExameCommand command) {
        AgendaExame agenda = new AgendaExame(
                command.tipoExameId(),
                command.diaSemana(),
                command.horaInicio(),
                command.horaFim(),
                command.duracaoSlotMinutos(),
                command.vagasPorSlot()
        );

        AgendaExame salva = agendaExameRepository.save(agenda);

        // Gera horários automaticamente para as próximas 4 semanas
        gerarHorariosParaAgenda(salva, LocalDate.now(), LocalDate.now().plusWeeks(4));

        return mapToAgendaOutput(salva);
    }

    @Override
    public void desativarAgendaExame(Long agendaExameId) {
        AgendaExameId id = new AgendaExameId(agendaExameId);
        AgendaExame agenda = agendaExameRepository.findById(id)
                .orElseThrow(() -> new AgendaNotFoundException(
                        "Agenda de exame não encontrada com ID: " + agendaExameId));

        agenda.desativar();
        agendaExameRepository.save(agenda);
    }

    @Override
    public void gerarHorariosExame(GerarHorariosExameCommand command) {
        AgendaExameId agendaId = new AgendaExameId(command.agendaExameId());
        AgendaExame agenda = agendaExameRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException(
                        "Agenda de exame não encontrada com ID: " + command.agendaExameId()));

        gerarHorariosParaAgenda(agenda, command.dataInicio(), command.dataFim());
    }

    @Override
    public List<HorarioExameDisponivelOutput> buscarHorariosExameDisponiveis(BuscarHorariosExameQuery query) {
        LocalDateTime inicio = query.dataInicio().atStartOfDay();
        LocalDateTime fim = query.dataFim().atTime(23, 59, 59);

        return horarioExameRepository
                .findDisponiveisPorTipoExameEPeriodo(query.tipoExameId(), inicio, fim)
                .stream()
                .map(this::mapToHorarioOutput)
                .collect(Collectors.toList());
    }

    @Override
    public AgendamentoExameOutput agendarExame(AgendarExameCommand command) {
        // 1. Busca o horário com lock pessimista (SELECT FOR UPDATE)
        HorarioExameDisponivelId horarioId = new HorarioExameDisponivelId(command.horarioExameDisponivelId());
        HorarioExameDisponivel horario = horarioExameRepository.findByIdParaReserva(horarioId)
                .orElseThrow(() -> new HorarioIndisponivelException(
                        "Horário de exame não encontrado com ID: " + command.horarioExameDisponivelId()));

        // 2. Verifica disponibilidade (regra de negócio no domínio)
        if (!horario.isDisponivel()) {
            throw new HorarioIndisponivelException("Não há vagas disponíveis neste horário de exame");
        }

        // 3. Cria o agendamento no domínio
        AgendamentoExame agendamento = new AgendamentoExame(
                horarioId,
                command.solicitacaoExameId(),
                horario.getTipoExameId(),
                horario.getDataHora()
        );

        // 4. Reserva a vaga (incrementa contador, regra no domínio)
        horario.reservar();

        // 5. Persiste
        AgendamentoExame salvo = agendamentoExameRepository.save(agendamento);
        horarioExameRepository.save(horario);

        // 6. Publica evento para MS-Exames
        exameEventPublisher.publicarExameAgendado(salvo);

        return mapToAgendamentoOutput(salvo);
    }

    @Override
    public void cancelarAgendamentoExame(Long agendamentoId) {
        AgendamentoExameId id = new AgendamentoExameId(agendamentoId);
        AgendamentoExame agendamento = agendamentoExameRepository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException(
                        "Agendamento de exame não encontrado com ID: " + agendamentoId));

        // 1. Cancela o agendamento (regra de negócio no domínio)
        agendamento.cancelar();
        agendamentoExameRepository.save(agendamento);

        // 2. Libera a vaga no horário (com lock)
        HorarioExameDisponivel horario = horarioExameRepository
                .findByIdParaReserva(agendamento.getHorarioExameId())
                .orElse(null);

        if (horario != null) {
            horario.liberar();
            horarioExameRepository.save(horario);
        }

        // 3. Publica evento para MS-Exames
        exameEventPublisher.publicarExameCancelado(agendamento);
    }

    private void gerarHorariosParaAgenda(AgendaExame agenda, LocalDate dataInicio, LocalDate dataFim) {
        List<HorarioExameDisponivel> horarios = new ArrayList<>();

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            List<LocalDateTime> slots = agenda.gerarHorarios(dataAtual);

            for (LocalDateTime slot : slots) {
                HorarioExameDisponivel horario = new HorarioExameDisponivel(
                        agenda.getId(),
                        agenda.getTipoExameId(),
                        slot,
                        agenda.getVagasPorSlot()
                );
                horarios.add(horario);
            }

            dataAtual = dataAtual.plusDays(1);
        }

        if (!horarios.isEmpty()) {
            horarioExameRepository.saveAll(horarios);
        }
    }

    private AgendaExameOutput mapToAgendaOutput(AgendaExame a) {
        return new AgendaExameOutput(
                a.getId().getValue(),
                a.getTipoExameId(),
                a.getDiaSemana().name(),
                a.getHoraInicio().toString(),
                a.getHoraFim().toString(),
                a.getDuracaoSlotMinutos(),
                a.getVagasPorSlot(),
                a.isAtiva()
        );
    }

    private HorarioExameDisponivelOutput mapToHorarioOutput(HorarioExameDisponivel h) {
        return new HorarioExameDisponivelOutput(
                h.getId().getValue(),
                h.getTipoExameId(),
                h.getDataHora(),
                h.getVagasRestantes()
        );
    }

    private AgendamentoExameOutput mapToAgendamentoOutput(AgendamentoExame a) {
        return new AgendamentoExameOutput(
                a.getId().getValue(),
                a.getHorarioExameId().getValue(),
                a.getSolicitacaoExameId(),
                a.getTipoExameId(),
                a.getDataHora(),
                a.getStatus().name(),
                a.getDataCriacao()
        );
    }
}
