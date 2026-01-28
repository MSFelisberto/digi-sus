package br.com.dgs.agendamento.infrastructure.messaging.adapters;

import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.domain.model.Consulta;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.ConsultaDTO;
import br.com.dgs.commons.dtos.HistoricoEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;

    public NotificationServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notificarAgendamento(Consulta consulta) {
        ConsultaDTO dto = toDTO(consulta);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_AGENDAR,
                dto
        );
        log.info("Notificação de agendamento enviada para consulta ID: {}",
                consulta.getId().getValue());

        HistoricoEventDTO historicoEvent = toHistoricoEventDTO(consulta, "AGENDADA");
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_HISTORICO,
                historicoEvent
        );
        log.info("Evento de histórico (AGENDADA) enviado para consulta ID: {}",
                consulta.getId().getValue());
    }

    @Override
    public void notificarCancelamento(Consulta consulta) {
        ConsultaDTO dto = toDTO(consulta);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_CANCELAR,
                dto
        );
        log.info("Notificação de cancelamento enviada para consulta ID: {}",
                consulta.getId().getValue());

        HistoricoEventDTO historicoEvent = toHistoricoEventDTO(consulta, "CANCELADA");
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_HISTORICO,
                historicoEvent
        );
        log.info("Evento de histórico (CANCELADA) enviado para consulta ID: {}",
                consulta.getId().getValue());
    }

    @Override
    public void notificarReagendamento(Consulta consulta) {
        ConsultaDTO dto = toDTO(consulta);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_REAGENDAR,
                dto
        );
        log.info("Notificação de reagendamento enviada para consulta ID: {}",
                consulta.getId().getValue());

        HistoricoEventDTO historicoEvent = toHistoricoEventDTO(consulta, "REAGENDADA");
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_HISTORICO,
                historicoEvent
        );
        log.info("Evento de histórico (REAGENDADA) enviado para consulta ID: {}",
                consulta.getId().getValue());
    }

    private ConsultaDTO toDTO(Consulta consulta) {
        return new ConsultaDTO(
                consulta.getPacienteId().getValue(),
                consulta.getMedicoId().getValue(),
                consulta.getDataHora(),
                consulta.getEspecialidade().getValue()
        );
    }

    private HistoricoEventDTO toHistoricoEventDTO(Consulta consulta, String tipoEvento) {
        return new HistoricoEventDTO(
                consulta.getId().getValue(),
                consulta.getPacienteId().getValue(),
                consulta.getMedicoId().getValue(),
                consulta.getDataHora(),
                consulta.getEspecialidade().getValue(),
                tipoEvento
        );
    }
}
