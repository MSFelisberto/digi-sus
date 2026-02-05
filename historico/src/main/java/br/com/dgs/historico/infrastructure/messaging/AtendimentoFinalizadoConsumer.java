package br.com.dgs.historico.infrastructure.messaging;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.AtendimentoFinalizadoEventDTO;
import br.com.dgs.historico.application.dto.RegistrarAtendimentoFinalizadoCommand;
import br.com.dgs.historico.application.ports.inbound.HistoricoUseCase;
import br.com.dgs.historico.domain.exception.HistoricoBusinessException;
import br.com.dgs.historico.domain.exception.HistoricoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtendimentoFinalizadoConsumer {

    private final HistoricoUseCase historicoUseCase;

    public AtendimentoFinalizadoConsumer(HistoricoUseCase historicoUseCase) {
        this.historicoUseCase = historicoUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_ATENDIMENTO_FINALIZADO)
    public void consumeAtendimentoFinalizado(AtendimentoFinalizadoEventDTO eventDTO) {
        log.info("[HISTORICO] Recebido evento de atendimento finalizado - AtendimentoId: {}, ConsultaId: {}, PacienteId: {}",
                eventDTO.atendimentoId(), eventDTO.consultaId(), eventDTO.pacienteId());

        try {
            RegistrarAtendimentoFinalizadoCommand command = new RegistrarAtendimentoFinalizadoCommand(
                    eventDTO.atendimentoId(),
                    eventDTO.consultaId(),
                    eventDTO.pacienteId(),
                    eventDTO.medicoId(),
                    eventDTO.anamnese(),
                    eventDTO.condutaMedica(),
                    eventDTO.dataHoraInicio(),
                    eventDTO.dataHoraFim()
            );

            historicoUseCase.registrarAtendimentoFinalizado(command);

            log.info("[HISTORICO] Histórico atualizado com sucesso para consulta {} após atendimento {}",
                    eventDTO.consultaId(), eventDTO.atendimentoId());

        } catch (HistoricoNotFoundException e) {
            log.warn("[HISTORICO] Histórico não encontrado para atualizar com atendimento: {}", e.getMessage());
        } catch (HistoricoBusinessException e) {
            log.warn("[HISTORICO] Erro de negócio ao processar atendimento finalizado: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[HISTORICO] Erro inesperado ao processar atendimento finalizado: {}", e.getMessage(), e);
        }
    }
}
