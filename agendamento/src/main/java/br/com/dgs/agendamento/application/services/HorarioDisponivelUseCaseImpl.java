package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.AutoAgendarCommand;
import br.com.dgs.agendamento.application.dto.BuscarHorariosQuery;
import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.HorarioDisponivelOutput;
import br.com.dgs.agendamento.application.ports.inbound.HorarioDisponivelUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.application.ports.outbound.HorarioDisponivelRepository;
import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.application.ports.outbound.PacienteService;
import br.com.dgs.agendamento.domain.exception.HorarioIndisponivelException;
import br.com.dgs.agendamento.domain.exception.PacienteNotFoundException;
import br.com.dgs.agendamento.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HorarioDisponivelUseCaseImpl implements HorarioDisponivelUseCase {

    private final HorarioDisponivelRepository horarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PacienteService pacienteService;
    private final NotificationService notificationService;

    public HorarioDisponivelUseCaseImpl(HorarioDisponivelRepository horarioRepository,
                                        ConsultaRepository consultaRepository,
                                        PacienteService pacienteService,
                                        NotificationService notificationService) {
        this.horarioRepository = horarioRepository;
        this.consultaRepository = consultaRepository;
        this.pacienteService = pacienteService;
        this.notificationService = notificationService;
    }

    @Override
    public List<HorarioDisponivelOutput> buscarHorariosDisponiveis(BuscarHorariosQuery query) {
        Especialidade especialidade = new Especialidade(query.especialidade());
        LocalDateTime inicio = query.dataInicio().atStartOfDay();
        LocalDateTime fim = query.dataFim().atTime(23, 59, 59);

        return horarioRepository.findDisponiveisPorEspecialidadeEPeriodo(especialidade, inicio, fim)
                .stream()
                .map(this::mapToHorarioOutput)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaOutput autoAgendar(AutoAgendarCommand command) {
        // 1. Busca o horário com lock (intenção de reserva)
        HorarioDisponivelId horarioId = new HorarioDisponivelId(command.horarioDisponivelId());
        HorarioDisponivel horario = horarioRepository.findByIdParaReserva(horarioId)
                .orElseThrow(() -> new HorarioIndisponivelException(
                        "Horário não encontrado com ID: " + command.horarioDisponivelId()));

        // 2. Verifica disponibilidade (regra de negócio no domínio)
        if (!horario.isDisponivel()) {
            throw new HorarioIndisponivelException("Horário já está ocupado");
        }

        // 3. Valida que o paciente existe
        PacienteId pacienteId = new PacienteId(command.currentUser().getId());
        if (!pacienteService.exists(pacienteId)) {
            throw new PacienteNotFoundException(
                    "Paciente não encontrado com ID: " + command.currentUser().getId());
        }

        // 4. Cria a Consulta no domínio usando dados do horário
        Consulta consulta = new Consulta(
                pacienteId,
                horario.getMedicoId(),
                horario.getDataHora(),
                horario.getEspecialidade()
        );

        // 5. Salva a consulta
        Consulta consultaSalva = consultaRepository.save(consulta);

        // 6. Reserva o horário (lógica de negócio no domínio)
        horario.reservar(consultaSalva.getId());

        // 7. Salva o horário atualizado
        horarioRepository.save(horario);

        // 8. Notifica
        notificationService.notificarAgendamento(consultaSalva);

        // 9. Retorna output
        return new ConsultaOutput(
                consultaSalva.getId().getValue(),
                consultaSalva.getPacienteId().getValue(),
                consultaSalva.getMedicoId().getValue(),
                consultaSalva.getDataHora(),
                consultaSalva.getEspecialidade().getValue(),
                consultaSalva.getStatus().name(),
                consultaSalva.getTipoConsulta() != null ? consultaSalva.getTipoConsulta().name() : null,
                consultaSalva.getPrioridade() != null ? consultaSalva.getPrioridade().name() : null,
                consultaSalva.getTriagemId()
        );
    }

    private HorarioDisponivelOutput mapToHorarioOutput(HorarioDisponivel horario) {
        return new HorarioDisponivelOutput(
                horario.getId().getValue(),
                horario.getMedicoId().getValue(),
                horario.getDataHora(),
                horario.getEspecialidade().getValue()
        );
    }
}