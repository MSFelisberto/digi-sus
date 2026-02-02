package br.com.dgs.triagem.infrastructure.configuration;

import br.com.dgs.commons.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RabbitConfig.EXCHANGE_NAME);
    }

    @Bean
    public Queue triagemAtendimentoQueue() {
        return new Queue(RabbitConfig.QUEUE_TRIAGEM_ATENDIMENTO, true);
    }

    @Bean
    public Queue triagemHistoricoQueue() {
        return new Queue(RabbitConfig.QUEUE_TRIAGEM_HISTORICO, true);
    }

    @Bean
    public Binding triagemAtendimentoBinding(Queue triagemAtendimentoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(triagemAtendimentoQueue)
                .to(exchange)
                .with(RabbitConfig.ROUTING_KEY_TRIAGEM_ATENDIMENTO);
    }

    @Bean
    public Binding triagemHistoricoBinding(Queue triagemHistoricoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(triagemHistoricoQueue)
                .to(exchange)
                .with(RabbitConfig.ROUTING_KEY_TRIAGEM_HISTORICO);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(RabbitAdmin rabbitAdmin) {
        return event -> {
            log.info("Inicializando filas do RabbitMQ...");
            rabbitAdmin.initialize();
            log.info("Filas do RabbitMQ criadas com sucesso!");
        };
    }
}
