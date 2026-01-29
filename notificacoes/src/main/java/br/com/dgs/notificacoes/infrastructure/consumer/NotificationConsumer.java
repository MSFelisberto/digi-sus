package br.com.dgs.notificacoes.infrastructure.consumer;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.ConsultaDTO;
import br.com.dgs.notificacoes.application.usecases.ConsultaInput;
import br.com.dgs.notificacoes.application.usecases.SendNotificationUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    private final SendNotificationUseCase sendNotificationUseCase;

    public NotificationConsumer(SendNotificationUseCase sendNotificationUseCase) {
        this.sendNotificationUseCase = sendNotificationUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_AGENDAR)
    public void consumeAgendar(ConsultaDTO consultaDTO) {
        log.info("[AGENDAR] Mensagem recebida: {}", consultaDTO);
        this.sendNotificationUseCase.sendNotification(new ConsultaInput(
                consultaDTO.pacienteId(),
                consultaDTO.medicoId(),
                consultaDTO.dataHora(),
                consultaDTO.especialidade()
        ), "agendamento");
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_CANCELAR)
    public void consumeCancelar(ConsultaDTO consultaDTO) {
        log.info("[CANCELAR] Mensagem recebida: {}", consultaDTO);
        this.sendNotificationUseCase.sendNotification(new ConsultaInput(
                consultaDTO.pacienteId(),
                consultaDTO.medicoId(),
                consultaDTO.dataHora(),
                consultaDTO.especialidade()
        ), "cancelamento");
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_REAGENDAR)
    public void consumeReagendar(ConsultaDTO consultaDTO) {
        log.info("[REAGENDAR] Mensagem recebida: {}", consultaDTO);
        this.sendNotificationUseCase.sendNotification(new ConsultaInput(
                consultaDTO.pacienteId(),
                consultaDTO.medicoId(),
                consultaDTO.dataHora(),
                consultaDTO.especialidade()
        ), "reagendamento");
    }


}

