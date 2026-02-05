package br.com.dgs.atendimento.infrastructure.configuration;

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

    // Atendimento Finalizado
    @Bean
    public Queue atendimentoFinalizadoQueue() {
        return new Queue(RabbitConfig.QUEUE_ATENDIMENTO_FINALIZADO, true);
    }

    @Bean
    public Binding bindingAtendimentoFinalizado(Queue atendimentoFinalizadoQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(atendimentoFinalizadoQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_ATENDIMENTO_FINALIZADO);
    }

    // Atendimento Exame Solicitar
    @Bean
    public Queue atendimentoExameSolicitarQueue() {
        return new Queue(RabbitConfig.QUEUE_ATENDIMENTO_EXAME_SOLICITAR, true);
    }

    @Bean
    public Binding bindingAtendimentoExameSolicitar(Queue atendimentoExameSolicitarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(atendimentoExameSolicitarQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_ATENDIMENTO_EXAME_SOLICITAR);
    }
}
