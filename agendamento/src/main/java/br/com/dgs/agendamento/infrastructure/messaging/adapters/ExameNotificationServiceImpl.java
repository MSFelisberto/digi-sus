package br.com.dgs.agendamento.infrastructure.messaging.adapters;

import br.com.dgs.agendamento.application.ports.outbound.ExameNotificationService;
import br.com.dgs.agendamento.domain.model.exame.SolicitacaoExame;
import br.com.dgs.agendamento.domain.model.exame.TipoExame;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.ExameEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ExameNotificationServiceImpl implements ExameNotificationService {

    private final RabbitTemplate rabbitTemplate;

    public ExameNotificationServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notificarSolicitacao(SolicitacaoExame solicitacao, TipoExame tipoExame) {
        ExameEventDTO dto = new ExameEventDTO(
                solicitacao.getId().getValue(),
                solicitacao.getPacienteId().getValue(),
                solicitacao.getMedicoId().getValue(),
                tipoExame.getNome(),
                solicitacao.getPrioridade().name(),
                null,
                "SOLICITADA"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_EXAME_SOLICITAR,
                dto
        );
        log.info("Notificação de solicitação de exame enviada. Solicitação ID: {}",
                solicitacao.getId().getValue());
    }

    @Override
    public void notificarAgendamento(SolicitacaoExame solicitacao, TipoExame tipoExame,
                                     LocalDateTime dataHora) {
        ExameEventDTO dto = new ExameEventDTO(
                solicitacao.getId().getValue(),
                solicitacao.getPacienteId().getValue(),
                solicitacao.getMedicoId().getValue(),
                tipoExame.getNome(),
                solicitacao.getPrioridade().name(),
                dataHora,
                "AGENDADA"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_EXAME_AGENDAR,
                dto
        );
        log.info("Notificação de agendamento de exame enviada. Solicitação ID: {}",
                solicitacao.getId().getValue());
    }

    @Override
    public void notificarCancelamento(SolicitacaoExame solicitacao, TipoExame tipoExame) {
        ExameEventDTO dto = new ExameEventDTO(
                solicitacao.getId().getValue(),
                solicitacao.getPacienteId().getValue(),
                solicitacao.getMedicoId().getValue(),
                tipoExame.getNome(),
                solicitacao.getPrioridade().name(),
                null,
                "CANCELADA"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_EXAME_CANCELAR,
                dto
        );
        log.info("Notificação de cancelamento de exame enviada. Solicitação ID: {}",
                solicitacao.getId().getValue());
    }
}