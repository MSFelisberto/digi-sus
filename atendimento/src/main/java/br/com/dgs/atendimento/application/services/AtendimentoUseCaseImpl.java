package br.com.dgs.atendimento.application.services;

import br.com.dgs.atendimento.application.dto.*;
import br.com.dgs.atendimento.application.ports.inbound.AtendimentoUseCase;
import br.com.dgs.atendimento.application.ports.outbound.AtendimentoEventPublisher;
import br.com.dgs.atendimento.application.ports.outbound.AtendimentoRepository;
import br.com.dgs.atendimento.application.ports.outbound.ConsultaService;
import br.com.dgs.atendimento.domain.exception.AtendimentoBusinessException;
import br.com.dgs.atendimento.domain.exception.AtendimentoNotFoundException;
import br.com.dgs.atendimento.domain.exception.ConsultaInvalidaException;
import br.com.dgs.atendimento.domain.model.*;

public class AtendimentoUseCaseImpl implements AtendimentoUseCase {

    private final AtendimentoRepository atendimentoRepository;
    private final ConsultaService consultaService;
    private final AtendimentoEventPublisher eventPublisher;

    public AtendimentoUseCaseImpl(AtendimentoRepository atendimentoRepository,
                                   ConsultaService consultaService,
                                   AtendimentoEventPublisher eventPublisher) {
        this.atendimentoRepository = atendimentoRepository;
        this.consultaService = consultaService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AtendimentoOutput iniciarAtendimento(IniciarAtendimentoCommand command) {
        ConsultaId consultaId = new ConsultaId(command.consultaId());

        if (atendimentoRepository.existsByConsultaId(consultaId)) {
            throw new AtendimentoBusinessException("Já existe um atendimento para esta consulta");
        }

        ConsultaService.ConsultaInfo consultaInfo = consultaService.buscarConsulta(consultaId);

        if (!"AGENDADA".equals(consultaInfo.status())) {
            throw new ConsultaInvalidaException(
                    "Consulta deve estar com status AGENDADA para iniciar atendimento. Status atual: " + consultaInfo.status());
        }

        if (!consultaInfo.medicoId().equals(command.medicoId())) {
            throw new ConsultaInvalidaException("Apenas o médico da consulta pode iniciar o atendimento");
        }

        Atendimento atendimento = new Atendimento(
                consultaId,
                new PacienteId(consultaInfo.pacienteId()),
                new MedicoId(command.medicoId())
        );

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return mapToOutput(salvo);
    }

    @Override
    public AtendimentoOutput finalizarAtendimento(FinalizarAtendimentoCommand command) {
        AtendimentoId atendimentoId = new AtendimentoId(command.atendimentoId());

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new AtendimentoNotFoundException(
                        "Atendimento não encontrado com ID: " + command.atendimentoId()));

        Anamnese anamnese = new Anamnese(command.anamnese());
        CondutaMedica condutaMedica = new CondutaMedica(command.condutaMedica());

        atendimento.finalizar(anamnese, condutaMedica);

        Atendimento finalizado = atendimentoRepository.save(atendimento);

        consultaService.marcarComoRealizada(atendimento.getConsultaId());

        eventPublisher.publicarAtendimentoFinalizado(finalizado);

        return mapToOutput(finalizado);
    }

    @Override
    public void solicitarExame(SolicitarExameCommand command) {
        AtendimentoId atendimentoId = new AtendimentoId(command.atendimentoId());

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new AtendimentoNotFoundException(
                        "Atendimento não encontrado com ID: " + command.atendimentoId()));

        atendimento.validarPodeSolicitarExame();

        eventPublisher.publicarExameSolicitado(
                atendimento.getId().getValue(),
                atendimento.getConsultaId().getValue(),
                atendimento.getPacienteId().getValue(),
                atendimento.getMedicoId().getValue(),
                command.tipoExame(),
                command.prioridade().toUpperCase(),
                command.observacoes()
        );
    }

    @Override
    public AtendimentoOutput buscarPorId(Long atendimentoId) {
        AtendimentoId id = new AtendimentoId(atendimentoId);

        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new AtendimentoNotFoundException(
                        "Atendimento não encontrado com ID: " + atendimentoId));

        return mapToOutput(atendimento);
    }

    @Override
    public AtendimentoOutput buscarPorConsultaId(Long consultaId) {
        ConsultaId id = new ConsultaId(consultaId);

        Atendimento atendimento = atendimentoRepository.findByConsultaId(id)
                .orElseThrow(() -> new AtendimentoNotFoundException(
                        "Atendimento não encontrado para consulta ID: " + consultaId));

        return mapToOutput(atendimento);
    }

    private AtendimentoOutput mapToOutput(Atendimento atendimento) {
        return new AtendimentoOutput(
                atendimento.getId().getValue(),
                atendimento.getConsultaId().getValue(),
                atendimento.getPacienteId().getValue(),
                atendimento.getMedicoId().getValue(),
                atendimento.getAnamnese() != null ? atendimento.getAnamnese().getValue() : null,
                atendimento.getCondutaMedica() != null ? atendimento.getCondutaMedica().getValue() : null,
                atendimento.getDataHoraInicio(),
                atendimento.getDataHoraFim(),
                atendimento.getStatus().name()
        );
    }
}
