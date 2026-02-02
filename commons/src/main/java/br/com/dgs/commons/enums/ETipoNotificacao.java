package br.com.dgs.commons.enums;

public enum ETipoNotificacao {

    AGENDAR("notificacao.agendar"),
    CANCELAR("notificacao.cancelar"),
    REAGENDAR("notificacao.reagendar"),
    HISTORICO("notificacao.historico"),
    EXAME_SOLICITAR("notificacao.exame.solicitar"),
    EXAME_AGENDAR("notificacao.exame.agendar"),
    EXAME_CANCELAR("notificacao.exame.cancelar");

    private final String routingKey;

    ETipoNotificacao(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
