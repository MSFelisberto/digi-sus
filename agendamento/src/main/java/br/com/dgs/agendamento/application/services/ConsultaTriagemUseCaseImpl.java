package br.com.dgs.agendamento.application.services;

import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.CriarConsultaTriagemCommand;
import br.com.dgs.agendamento.application.ports.inbound.ConsultaTriagemUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.application.ports.outbound.HorarioDisponivelRepository;
import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.domain.exception.ConsultaBusinessException;
import br.com.dgs.agendamento.domain.model.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class ConsultaTriagemUseCaseImpl implements ConsultaTriagemUseCase {

    private final ConsultaRepository consultaRepository;
    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final NotificationService notificationService;

    public ConsultaTriagemUseCaseImpl(ConsultaRepository consultaRepository,
                                       HorarioDisponivelRepository horarioDisponivelRepository,
                                       NotificationService notificationService) {
        this.consultaRepository = consultaRepository;
        this.horarioDisponivelRepository = horarioDisponivelRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ConsultaOutput criarConsultaTriagem(CriarConsultaTriagemCommand command) {
        // 1. Idempotência: verificar se já existe consulta para esta triagem
        Optional<Consulta> existente = consultaRepository.findByTriagemId(command.triagemId());
        if (existente.isPresent()) {
            log.warn("[TRIAGEM-ATENDIMENTO] Consulta já existe para triagem ID: {}. Retornando existente.", command.triagemId());
            return mapToOutput(existente.get());
        }

        PacienteId pacienteId = new PacienteId(command.pacienteId());
        Especialidade especialidade = new Especialidade(command.especialidade());
        Prioridade prioridade = Prioridade.valueOf(command.prioridade());

        LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();
        LocalDateTime fimHoje = LocalDate.now().atTime(23, 59, 59);

        // 2. Tentar encontrar slot livre hoje para a especialidade
        Optional<HorarioDisponivel> slotOpt = horarioDisponivelRepository
                .findPrimeiroDisponivelPorEspecialidadeHoje(especialidade, inicioHoje, fimHoje);

        Consulta consulta;

        if (slotOpt.isPresent()) {
            // 3. Slot encontrado: criar consulta REGULAR
            HorarioDisponivel slot = slotOpt.get();
            consulta = Consulta.criarConsultaTriagem(
                    pacienteId,
                    slot.getMedicoId(),
                    slot.getDataHora(),
                    especialidade,
                    TipoConsulta.REGULAR,
                    prioridade,
                    command.triagemId()
            );

            Consulta consultaSalva = consultaRepository.save(consulta);
            slot.reservar(consultaSalva.getId());
            horarioDisponivelRepository.save(slot);
            consulta = consultaSalva;

            log.info("[TRIAGEM-ATENDIMENTO] Consulta REGULAR criada com sucesso. ID: {}, Triagem: {}",
                    consulta.getId().getValue(), command.triagemId());
        } else {
            // 4. Sem slot: buscar qualquer médico da especialidade para encaixe
            Optional<MedicoId> medicoOpt = horarioDisponivelRepository
                    .findMedicoIdPorEspecialidadeEData(especialidade, inicioHoje, fimHoje);

            if (medicoOpt.isEmpty()) {
                throw new ConsultaBusinessException(
                        "Nenhum médico disponível na especialidade " + command.especialidade() + " para hoje");
            }

            consulta = Consulta.criarConsultaTriagem(
                    pacienteId,
                    medicoOpt.get(),
                    LocalDateTime.now(),
                    especialidade,
                    TipoConsulta.ENCAIXE,
                    prioridade,
                    command.triagemId()
            );

            consulta = consultaRepository.save(consulta);

            log.info("[TRIAGEM-ATENDIMENTO] Consulta ENCAIXE criada com sucesso. ID: {}, Triagem: {}",
                    consulta.getId().getValue(), command.triagemId());
        }

        // 5. Notificar
        notificationService.notificarAgendamento(consulta);

        // 6. Retornar output
        return mapToOutput(consulta);
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
