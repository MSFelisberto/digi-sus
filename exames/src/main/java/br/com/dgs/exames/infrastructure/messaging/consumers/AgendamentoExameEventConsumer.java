package br.com.dgs.exames.infrastructure.messaging.consumers;

import br.com.dgs.exames.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.AgendamentoExameEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AgendamentoExameEventConsumer {

    private final SolicitacaoExameUseCase solicitacaoExameUseCase;

    public AgendamentoExameEventConsumer(SolicitacaoExameUseCase solicitacaoExameUseCase) {
        this.solicitacaoExameUseCase = solicitacaoExameUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_AGENDAMENTO_EXAME_AGENDADO)
    public void processarExameAgendado(AgendamentoExameEventDTO event) {
        log.info("[EXAME-AGENDADO] Recebido evento de agendamento. SolicitacaoExameId: {}, DataHora: {}",
                event.solicitacaoExameId(), event.dataHora());

        try {
            solicitacaoExameUseCase.marcarComoAgendada(event.solicitacaoExameId(), event.dataHora());
            log.info("[EXAME-AGENDADO] Solicitação {} marcada como AGENDADA", event.solicitacaoExameId());
        } catch (Exception e) {
            log.error("[EXAME-AGENDADO] Erro ao processar evento para solicitação {}: {}",
                    event.solicitacaoExameId(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_AGENDAMENTO_EXAME_CANCELADO)
    public void processarExameCancelado(AgendamentoExameEventDTO event) {
        log.info("[EXAME-CANCELADO] Recebido evento de cancelamento. SolicitacaoExameId: {}",
                event.solicitacaoExameId());

        try {
            solicitacaoExameUseCase.retornarParaPendente(event.solicitacaoExameId());
            log.info("[EXAME-CANCELADO] Solicitação {} retornada para PENDENTE", event.solicitacaoExameId());
        } catch (Exception e) {
            log.error("[EXAME-CANCELADO] Erro ao processar evento para solicitação {}: {}",
                    event.solicitacaoExameId(), e.getMessage(), e);
        }
    }
}
