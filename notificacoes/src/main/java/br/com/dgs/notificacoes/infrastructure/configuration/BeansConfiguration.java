package br.com.dgs.notificacoes.infrastructure.configuration;

import br.com.dgs.notificacoes.application.usecases.SendNotificationUseCaseImpl;
import br.com.dgs.notificacoes.infrastructure.consumer.NotificationConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public NotificationConsumer agendamentoUseCase(SendNotificationUseCaseImpl sendNotificationUseCaseImpl) {
        return new NotificationConsumer(sendNotificationUseCaseImpl);
    }
}
