package br.com.dgs.agendamento.infrastructure.messaging.adapters;

import br.com.dgs.agendamento.application.ports.outbound.ExameEventPublisher;
import br.com.dgs.agendamento.domain.model.AgendamentoExame;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.AgendamentoExameEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExameEventPublisherImpl implements ExameEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ExameEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarExameAgendado(AgendamentoExame agendamento) {
        AgendamentoExameEventDTO dto = new AgendamentoExameEventDTO(
                agendamento.getId().getValue(),
                agendamento.getSolicitacaoExameId(),
                agendamento.getTipoExameId(),
                agendamento.getDataHora(),
                "AGENDADO"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_AGENDAMENTO_EXAME_AGENDADO,
                dto
        );
        log.info("Evento de exame agendado publicado. AgendamentoExameId: {}, SolicitacaoExameId: {}",
                agendamento.getId().getValue(), agendamento.getSolicitacaoExameId());
    }

    @Override
    public void publicarExameCancelado(AgendamentoExame agendamento) {
        AgendamentoExameEventDTO dto = new AgendamentoExameEventDTO(
                agendamento.getId().getValue(),
                agendamento.getSolicitacaoExameId(),
                agendamento.getTipoExameId(),
                agendamento.getDataHora(),
                "CANCELADO"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_AGENDAMENTO_EXAME_CANCELADO,
                dto
        );
        log.info("Evento de exame cancelado publicado. AgendamentoExameId: {}, SolicitacaoExameId: {}",
                agendamento.getId().getValue(), agendamento.getSolicitacaoExameId());
    }
}
