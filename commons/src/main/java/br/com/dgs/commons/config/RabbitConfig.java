package br.com.dgs.commons.config;

public class RabbitConfig {

    private RabbitConfig() { throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");}

    public static final String EXCHANGE_NAME = "notificacoes";
    public static final String QUEUE_AGENDAR = "notificacao.agendar.queue";
    public static final String QUEUE_CANCELAR = "notificacao.cancelar.queue";
    public static final String QUEUE_REAGENDAR = "notificacao.reagendar.queue";
    public static final String QUEUE_HISTORICO = "notificacao.historico.queue";
    public static final String QUEUE_TRIAGEM_ATENDIMENTO = "triagem.atendimento.queue";
    public static final String QUEUE_TRIAGEM_HISTORICO = "triagem.historico.queue";

    public static final String ROUTING_KEY_HISTORICO = "notificacao.historico";
    public static final String ROUTING_KEY_AGENDAR = "notificacao.agendar";
    public static final String ROUTING_KEY_CANCELAR = "notificacao.cancelar";
    public static final String ROUTING_KEY_REAGENDAR = "notificacao.reagendar";
    public static final String ROUTING_KEY_TRIAGEM_ATENDIMENTO = "triagem.atendimento";
    public static final String ROUTING_KEY_TRIAGEM_HISTORICO = "triagem.historico";

    public static final String QUEUE_EXAME_SOLICITAR = "notificacao.exame.queue";
    public static final String QUEUE_EXAME_AGENDAR = "notificacao.exame.agendar";
    public static final String QUEUE_EXAME_CANCELAR = "notificacao.exame.cancelar";

    public static final String ROUTING_KEY_EXAME_SOLICITAR = "notificacao.exame.solicitar";
    public static final String ROUTING_KEY_EXAME_AGENDAR = "notificacao.exame.agendar";
    public static final String ROUTING_KEY_EXAME_CANCELAR = "notificacao.exame.cancelar";

    // Atendimento
    public static final String QUEUE_ATENDIMENTO_FINALIZADO = "atendimento.finalizado.queue";
    public static final String QUEUE_ATENDIMENTO_EXAME_SOLICITAR = "atendimento.exame.solicitar.queue";

    public static final String ROUTING_KEY_ATENDIMENTO_FINALIZADO = "atendimento.finalizado";
    public static final String ROUTING_KEY_ATENDIMENTO_EXAME_SOLICITAR = "atendimento.exame.solicitar";

    // Agendamento de Exames (comunicação entre MS-Agendamento e MS-Exames)
    public static final String QUEUE_AGENDAMENTO_EXAME_AGENDADO = "agendamento.exame.agendado.queue";
    public static final String QUEUE_AGENDAMENTO_EXAME_CANCELADO = "agendamento.exame.cancelado.queue";

    public static final String ROUTING_KEY_AGENDAMENTO_EXAME_AGENDADO = "agendamento.exame.agendado";
    public static final String ROUTING_KEY_AGENDAMENTO_EXAME_CANCELADO = "agendamento.exame.cancelado";
}
