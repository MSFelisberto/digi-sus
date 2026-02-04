package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.AgendaOutput;
import br.com.dgs.agendamento.application.dto.AuthenticatedUser;
import br.com.dgs.agendamento.application.dto.CriarAgendaCommand;
import br.com.dgs.agendamento.application.dto.GerarHorariosCommand;
import br.com.dgs.agendamento.application.ports.inbound.AgendaUseCase;
import br.com.dgs.agendamento.application.ports.outbound.AgendaRepository;
import br.com.dgs.agendamento.application.ports.outbound.FuncionarioService;
import br.com.dgs.agendamento.application.ports.outbound.HorarioDisponivelRepository;
import br.com.dgs.agendamento.domain.exception.AgendaNotFoundException;
import br.com.dgs.agendamento.domain.exception.AuthorizationException;
import br.com.dgs.agendamento.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AgendaUseCaseImpl implements AgendaUseCase {

    private final AgendaRepository agendaRepository;
    private final HorarioDisponivelRepository horarioRepository;
    private final FuncionarioService funcionarioService;

    public AgendaUseCaseImpl(AgendaRepository agendaRepository,
                             HorarioDisponivelRepository horarioRepository,
                             FuncionarioService funcionarioService) {
        this.agendaRepository = agendaRepository;
        this.horarioRepository = horarioRepository;
        this.funcionarioService = funcionarioService;
    }

    @Override
    public AgendaOutput criarAgenda(CriarAgendaCommand command) {
        MedicoId medicoId = new MedicoId(command.medicoId());
        AuthenticatedUser user = command.currentUser();

        if (user.hasRole("MEDICO")) {
            if (!medicoId.getValue().equals(user.getId())) {
                throw new AuthorizationException(
                        "Médico só pode criar agenda para si mesmo. "
                        + "Seu ID: " + user.getId() + ", medicoId informado: " + medicoId.getValue());
            }
        } else if (user.hasRole("ADMIN")) {
            if (!funcionarioService.isMedico(medicoId)) {
                throw new AuthorizationException(
                        "O ID " + medicoId.getValue() + " não pertence a um funcionário do tipo MÉDICO.");
            }
        }

        Especialidade especialidade = new Especialidade(command.especialidade());

        Agenda agenda = new Agenda(
                medicoId,
                command.diaSemana(),
                command.horaInicio(),
                command.horaFim(),
                command.duracaoSlotMinutos(),
                especialidade
        );

        Agenda agendaSalva = agendaRepository.save(agenda);

        // Gera horários automaticamente para as próximas 4 semanas
        gerarHorariosParaAgenda(agendaSalva, LocalDate.now(), LocalDate.now().plusWeeks(4));

        return mapToOutput(agendaSalva);
    }

    @Override
    public void desativarAgenda(Long agendaId) {
        AgendaId id = new AgendaId(agendaId);
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new AgendaNotFoundException("Agenda não encontrada com ID: " + agendaId));

        agenda.desativar();
        agendaRepository.save(agenda);
    }

    @Override
    public List<AgendaOutput> listarAgendasPorMedico(Long medicoId) {
        MedicoId id = new MedicoId(medicoId);
        return agendaRepository.findByMedicoId(id)
                .stream()
                .map(this::mapToOutput)
                .collect(Collectors.toList());
    }

    @Override
    public void gerarHorarios(GerarHorariosCommand command) {
        AgendaId agendaId = new AgendaId(command.agendaId());
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException("Agenda não encontrada com ID: " + command.agendaId()));

        gerarHorariosParaAgenda(agenda, command.dataInicio(), command.dataFim());
    }

    private void gerarHorariosParaAgenda(Agenda agenda, LocalDate dataInicio, LocalDate dataFim) {
        List<HorarioDisponivel> horarios = new ArrayList<>();

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            List<LocalDateTime> slots = agenda.gerarHorarios(dataAtual);

            for (LocalDateTime slot : slots) {
                HorarioDisponivel horario = new HorarioDisponivel(
                        agenda.getId(),
                        agenda.getMedicoId(),
                        slot,
                        agenda.getEspecialidade()
                );
                horarios.add(horario);
            }

            dataAtual = dataAtual.plusDays(1);
        }

        if (!horarios.isEmpty()) {
            horarioRepository.saveAll(horarios);
        }
    }

    private AgendaOutput mapToOutput(Agenda agenda) {
        return new AgendaOutput(
                agenda.getId().getValue(),
                agenda.getMedicoId().getValue(),
                agenda.getDiaSemana().name(),
                agenda.getHoraInicio().toString(),
                agenda.getHoraFim().toString(),
                agenda.getDuracaoSlotMinutos(),
                agenda.getEspecialidade().getValue(),
                agenda.isAtiva()
        );
    }
}