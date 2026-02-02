package br.com.dgs.notificacoes.infrastructure.consumer;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.ConsultaDTO;
import br.com.dgs.commons.dtos.ExameEventDTO;
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

    @RabbitListener(queues = RabbitConfig.QUEUE_EXAME_SOLICITAR)
    public void consumeExameSolicitar(ExameEventDTO exameEvent) {
        log.info("[EXAME-SOLICITAR] Mensagem recebida: {}", exameEvent);
        log.info("[EXAME-SOLICITAR] Enviando e-mail de solicitação de exame '{}' para paciente ID: {}. " +
                        "Prioridade: {}. Médico solicitante ID: {}",
                exameEvent.tipoExameNome(),
                exameEvent.pacienteId(),
                exameEvent.prioridade(),
                exameEvent.medicoId());
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_EXAME_AGENDAR)
    public void consumeExameAgendar(ExameEventDTO exameEvent) {
        log.info("[EXAME-AGENDAR] Mensagem recebida: {}", exameEvent);
        log.info("[EXAME-AGENDAR] Enviando e-mail de agendamento de exame '{}' para paciente ID: {}. " +
                        "Data/Hora: {}. Prioridade: {}",
                exameEvent.tipoExameNome(),
                exameEvent.pacienteId(),
                exameEvent.dataHora(),
                exameEvent.prioridade());
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_EXAME_CANCELAR)
    public void consumeExameCancelar(ExameEventDTO exameEvent) {
        log.info("[EXAME-CANCELAR] Mensagem recebida: {}", exameEvent);
        log.info("[EXAME-CANCELAR] Enviando e-mail de cancelamento de exame '{}' para paciente ID: {}. " +
                        "Médico solicitante ID: {}",
                exameEvent.tipoExameNome(),
                exameEvent.pacienteId(),
                exameEvent.medicoId());
    }
}

