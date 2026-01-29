package br.com.dgs.notificacoes.infrastructure.configuration;

import br.com.dgs.commons.config.RabbitConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange notificacoesExchange() {
        return new TopicExchange(RabbitConfig.EXCHANGE_NAME);
    }

    @Bean
    public Queue agendarQueue() {
        return new Queue(RabbitConfig.QUEUE_AGENDAR, true);
    }

    @Bean
    public Queue cancelarQueue() {
        return new Queue(RabbitConfig.QUEUE_CANCELAR, true);
    }

    @Bean
    public Queue reagendarQueue() {
        return new Queue(RabbitConfig.QUEUE_REAGENDAR, true);
    }

    @Bean
    public Binding bindingAgendar(Queue agendarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(agendarQueue).to(notificacoesExchange).with(RabbitConfig.ROUTING_KEY_AGENDAR);
    }

    @Bean
    public Binding bindingCancelar(Queue cancelarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(cancelarQueue).to(notificacoesExchange).with(RabbitConfig.ROUTING_KEY_CANCELAR);
    }

    @Bean
    public Binding bindingReagendar(Queue reagendarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(reagendarQueue).to(notificacoesExchange).with(RabbitConfig.ROUTING_KEY_REAGENDAR);
    }

}
