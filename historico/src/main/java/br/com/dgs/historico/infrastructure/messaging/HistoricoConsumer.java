package br.com.dgs.historico.infrastructure.messaging;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.HistoricoEventDTO;
import br.com.dgs.historico.application.dto.AtualizarHistoricoCommand;
import br.com.dgs.historico.application.dto.CancelarHistoricoCommand;
import br.com.dgs.historico.application.dto.HistoricoOutput;
import br.com.dgs.historico.application.dto.RegistrarHistoricoCommand;
import br.com.dgs.historico.application.ports.inbound.HistoricoUseCase;
import br.com.dgs.historico.domain.exception.HistoricoBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HistoricoConsumer {

    private final HistoricoUseCase historicoUseCase;

    public HistoricoConsumer(HistoricoUseCase historicoUseCase) {
        this.historicoUseCase = historicoUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_HISTORICO)
    public void consumeHistoricoEvent(HistoricoEventDTO eventDTO) {
        log.info("[HISTORICO] Recebido evento: {} - Consulta ID: {}, Paciente: {}, Médico: {}, Data: {}",
                eventDTO.tipoEvento(), eventDTO.consultaId(), eventDTO.pacienteId(),
                eventDTO.medicoId(), eventDTO.dataHora());

        try {
            switch (eventDTO.tipoEvento()) {
                case "AGENDADA" -> processarAgendamento(eventDTO);
                case "CANCELADA" -> processarCancelamento(eventDTO);
                case "REAGENDADA" -> processarReagendamento(eventDTO);
                default -> log.warn("[HISTORICO] Tipo de evento desconhecido: {}", eventDTO.tipoEvento());
            }
        } catch (HistoricoBusinessException e) {
            log.warn("[HISTORICO] Erro de negócio ao processar evento: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[HISTORICO] Erro inesperado ao processar evento: {}", e.getMessage(), e);
            // Aqui você pode implementar lógica de retry ou enviar para DLQ
        }
    }

    private void processarAgendamento(HistoricoEventDTO eventDTO) {
        RegistrarHistoricoCommand command = new RegistrarHistoricoCommand(
                eventDTO.consultaId(),
                eventDTO.pacienteId(),
                eventDTO.medicoId(),
                eventDTO.dataHora(),
                eventDTO.especialidade()
        );

        HistoricoOutput output = historicoUseCase.registrarHistorico(command);
        log.info("[HISTORICO] Histórico registrado com sucesso - ID: {}, Consulta ID: {}",
                output.id(), output.consultaId());
    }

    private void processarCancelamento(HistoricoEventDTO eventDTO) {
        CancelarHistoricoCommand command = new CancelarHistoricoCommand(
                eventDTO.consultaId(),
                "Consulta cancelada"
        );

        historicoUseCase.cancelarHistorico(command);
        log.info("[HISTORICO] Histórico atualizado para CANCELADA - Consulta ID: {}",
                eventDTO.consultaId());
    }

    private void processarReagendamento(HistoricoEventDTO eventDTO) {
        AtualizarHistoricoCommand command = new AtualizarHistoricoCommand(
                eventDTO.consultaId(),
                eventDTO.dataHora(),
                eventDTO.medicoId(),
                eventDTO.especialidade()
        );

        HistoricoOutput output = historicoUseCase.atualizarHistorico(command);
        log.info("[HISTORICO] Histórico atualizado para REAGENDADA - ID: {}, Consulta ID: {}",
                output.id(), output.consultaId());
    }
}
