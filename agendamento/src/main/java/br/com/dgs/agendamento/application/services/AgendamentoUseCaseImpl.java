package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.*;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.application.ports.outbound.HorarioDisponivelRepository;
import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.application.ports.outbound.PacienteService;
import br.com.dgs.agendamento.domain.exception.AuthorizationException;
import br.com.dgs.agendamento.domain.exception.ConsultaNotFoundException;
import br.com.dgs.agendamento.domain.exception.HorarioIndisponivelException;
import br.com.dgs.agendamento.domain.exception.PacienteNotFoundException;
import br.com.dgs.agendamento.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class AgendamentoUseCaseImpl implements AgendamentoUseCase {

    private final ConsultaRepository consultaRepository;
    private final PacienteService pacienteService;
    private final NotificationService notificationService;
    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public AgendamentoUseCaseImpl(
            ConsultaRepository consultaRepository,
            PacienteService pacienteService,
            NotificationService notificationService,
            HorarioDisponivelRepository horarioDisponivelRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteService = pacienteService;
        this.notificationService = notificationService;
        this.horarioDisponivelRepository = horarioDisponivelRepository;
    }

    @Override
    public ConsultaOutput agendarConsulta(AgendarConsultaCommand command) {
        PacienteId pacienteId = new PacienteId(command.pacienteId());
        if (!pacienteService.exists(pacienteId)) {
            throw new PacienteNotFoundException("Paciente não encontrado com ID: " + command.pacienteId());
        }

        MedicoId medicoId = new MedicoId(command.medicoId());
        Especialidade especialidade = new Especialidade(command.especialidade());

        HorarioDisponivel horario = horarioDisponivelRepository
                .findByMedicoIdAndDataHoraParaReserva(medicoId, command.dataHora())
                .orElseThrow(() -> new HorarioIndisponivelException(
                        "Não existe horário disponível para o médico " + command.medicoId()
                        + " na data/hora " + command.dataHora()
                        + ". Verifique se a agenda do médico foi criada e os horários foram gerados."));

        if (!horario.isDisponivel()) {
            throw new HorarioIndisponivelException(
                    "Horário " + command.dataHora() + " do médico " + command.medicoId()
                    + " já está ocupado.");
        }

        Consulta consulta = new Consulta(
                pacienteId,
                medicoId,
                command.dataHora(),
                especialidade
        );

        Consulta consultaSalva = consultaRepository.save(consulta);

        horario.reservar(consultaSalva.getId());
        horarioDisponivelRepository.save(horario);

        notificationService.notificarAgendamento(consultaSalva);

        return mapToOutput(consultaSalva);
    }

    @Override
    public ConsultaOutput reagendarConsulta(ReagendarConsultaCommand command) {
        ConsultaId consultaId = new ConsultaId(command.consultaId());
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada com ID: " + command.consultaId()));

        MedicoId novoMedico = new MedicoId(command.medicoId());
        Especialidade novaEspecialidade = new Especialidade(command.especialidade());

        // Liberar o slot antigo (se existir)
        horarioDisponivelRepository.findByConsultaId(consultaId)
                .ifPresent(slotAntigo -> {
                    slotAntigo.liberar();
                    horarioDisponivelRepository.save(slotAntigo);
                });

        // Buscar e validar o novo slot com lock
        HorarioDisponivel novoSlot = horarioDisponivelRepository
                .findByMedicoIdAndDataHoraParaReserva(novoMedico, command.dataHora())
                .orElseThrow(() -> new HorarioIndisponivelException(
                        "Não existe horário disponível para o médico " + command.medicoId()
                        + " na data/hora " + command.dataHora()
                        + ". Verifique se a agenda do médico foi criada e os horários foram gerados."));

        if (!novoSlot.isDisponivel()) {
            throw new HorarioIndisponivelException(
                    "Horário " + command.dataHora() + " do médico " + command.medicoId()
                    + " já está ocupado.");
        }

        // Reagendar a consulta
        consulta.reagendar(command.dataHora(), novoMedico, novaEspecialidade);
        Consulta consultaReagendada = consultaRepository.save(consulta);

        // Reservar o novo slot
        novoSlot.reservar(consultaReagendada.getId());
        horarioDisponivelRepository.save(novoSlot);

        notificationService.notificarReagendamento(consultaReagendada);

        return mapToOutput(consultaReagendada);
    }

    @Override
    public void cancelarConsulta(CancelarConsultaCommand command) {
        ConsultaId consultaId = new ConsultaId(command.consultaId());
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada com ID: " + command.consultaId()));

        consulta.cancelar();

        consultaRepository.save(consulta);

        // Liberar o horário associado, se existir
        horarioDisponivelRepository.findByConsultaId(consultaId)
                .ifPresent(horario -> {
                    horario.liberar();
                    horarioDisponivelRepository.save(horario);
                });

        notificationService.notificarCancelamento(consulta);
    }

    @Override
    public List<ConsultaOutput> listarConsultasPorPaciente(ListarConsultasQuery query) {
        PacienteId pacienteId = new PacienteId(query.pacienteId());

        if (query.currentUser().hasRole("PACIENTE")) {
            PacienteId currentUserId = new PacienteId(query.currentUser().getId());
            if (!pacienteId.equals(currentUserId)) {
                throw new AuthorizationException("Acesso negado. Paciente só pode visualizar as próprias consultas.");
            }
        }

        return consultaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToOutput)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultaOutput> listarConsultasFuturas(ListarConsultasFuturasQuery query) {
        AuthenticatedUser user = query.currentUser();
        List<Consulta> consultas;

        if (user.hasRole("ADMIN")) {
            consultas = consultaRepository.findAllFuturas();
        } else if (user.hasRole("MEDICO")) {
            MedicoId medicoId = new MedicoId(user.getId());
            consultas = consultaRepository.findFuturasByMedicoId(medicoId);
        } else {
            PacienteId pacienteId = new PacienteId(user.getId());
            consultas = consultaRepository.findFuturasByPacienteId(pacienteId);
        }

        return consultas.stream()
                .map(this::mapToOutput)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaOutput buscarPorId(Long consultaId) {
        ConsultaId id = new ConsultaId(consultaId);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada com ID: " + consultaId));
        return mapToOutput(consulta);
    }

    @Override
    public ConsultaOutput marcarComoRealizada(Long consultaId) {
        ConsultaId id = new ConsultaId(consultaId);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada com ID: " + consultaId));

        consulta.marcarComoRealizada();
        Consulta consultaAtualizada = consultaRepository.save(consulta);

        return mapToOutput(consultaAtualizada);
    }

    @Override
    public ConsultaOutput marcarComoEmAtendimento(Long consultaId) {
        ConsultaId id = new ConsultaId(consultaId);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada com ID: " + consultaId));

        consulta.iniciarAtendimento();
        Consulta consultaAtualizada = consultaRepository.save(consulta);

        return mapToOutput(consultaAtualizada);
    }

    private ConsultaOutput mapToOutput(Consulta consulta) {
        return new ConsultaOutput(
                consulta.getId().getValue(),
                consulta.getPacienteId().getValue(),
                consulta.getMedicoId().getValue(),
                consulta.getDataHora(),
                consulta.getEspecialidade().getValue(),
                consulta.getStatus().name(),
                consulta.getTipoConsulta() != null ? consulta.getTipoConsulta().name() : null,
                consulta.getPrioridade() != null ? consulta.getPrioridade().name() : null,
                consulta.getTriagemId()
        );
    }
}
