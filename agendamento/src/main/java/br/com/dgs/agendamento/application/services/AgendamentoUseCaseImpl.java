package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.*;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.application.ports.outbound.PacienteService;
import br.com.dgs.agendamento.domain.exception.AuthorizationException;
import br.com.dgs.agendamento.domain.exception.ConsultaNotFoundException;
import br.com.dgs.agendamento.domain.exception.PacienteNotFoundException;
import br.com.dgs.agendamento.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class AgendamentoUseCaseImpl implements AgendamentoUseCase {

    private final ConsultaRepository consultaRepository;
    private final PacienteService pacienteService;
    private final NotificationService notificationService;

    public AgendamentoUseCaseImpl(
            ConsultaRepository consultaRepository,
            PacienteService pacienteService,
            NotificationService notificationService) {
        this.consultaRepository = consultaRepository;
        this.pacienteService = pacienteService;
        this.notificationService = notificationService;
    }

    @Override
    public ConsultaOutput agendarConsulta(AgendarConsultaCommand command) {
        PacienteId pacienteId = new PacienteId(command.pacienteId());
        if (!pacienteService.exists(pacienteId)) {
            throw new PacienteNotFoundException("Paciente não encontrado com ID: " + command.pacienteId());
        }

        MedicoId medicoId = new MedicoId(command.medicoId());
        Especialidade especialidade = new Especialidade(command.especialidade());

        Consulta consulta = new Consulta(
                pacienteId,
                medicoId,
                command.dataHora(),
                especialidade
        );

        Consulta consultaSalva = consultaRepository.save(consulta);

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

        consulta.reagendar(command.dataHora(), novoMedico, novaEspecialidade);

        Consulta consultaReagendada = consultaRepository.save(consulta);

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

    private ConsultaOutput mapToOutput(Consulta consulta) {
        return new ConsultaOutput(
                consulta.getId().getValue(),
                consulta.getPacienteId().getValue(),
                consulta.getMedicoId().getValue(),
                consulta.getDataHora(),
                consulta.getEspecialidade().getValue(),
                consulta.getStatus().name()
        );
    }
}
