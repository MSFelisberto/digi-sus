package br.com.dgs.exames.infrastructure.configuration;

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

    // Queue para receber solicitações de exames do atendimento
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

    // Queues para notificações de exames
    @Bean
    public Queue exameSolicitarQueue() {
        return new Queue(RabbitConfig.QUEUE_EXAME_SOLICITAR, true);
    }

    @Bean
    public Binding bindingExameSolicitar(Queue exameSolicitarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(exameSolicitarQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_EXAME_SOLICITAR);
    }

    @Bean
    public Queue exameAgendarQueue() {
        return new Queue(RabbitConfig.QUEUE_EXAME_AGENDAR, true);
    }

    @Bean
    public Binding bindingExameAgendar(Queue exameAgendarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(exameAgendarQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_EXAME_AGENDAR);
    }

    @Bean
    public Queue exameCancelarQueue() {
        return new Queue(RabbitConfig.QUEUE_EXAME_CANCELAR, true);
    }

    @Bean
    public Binding bindingExameCancelar(Queue exameCancelarQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(exameCancelarQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_EXAME_CANCELAR);
    }

    // Queues para receber eventos de agendamento do MS-Agendamento
    @Bean
    public Queue agendamentoExameAgendadoQueue() {
        return new Queue(RabbitConfig.QUEUE_AGENDAMENTO_EXAME_AGENDADO, true);
    }

    @Bean
    public Queue agendamentoExameCanceladoQueue() {
        return new Queue(RabbitConfig.QUEUE_AGENDAMENTO_EXAME_CANCELADO, true);
    }

    @Bean
    public Binding bindingAgendamentoExameAgendado(Queue agendamentoExameAgendadoQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(agendamentoExameAgendadoQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_AGENDAMENTO_EXAME_AGENDADO);
    }

    @Bean
    public Binding bindingAgendamentoExameCancelado(Queue agendamentoExameCanceladoQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(agendamentoExameCanceladoQueue)
                .to(notificacoesExchange)
                .with(RabbitConfig.ROUTING_KEY_AGENDAMENTO_EXAME_CANCELADO);
    }
}
