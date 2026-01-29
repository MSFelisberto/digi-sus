package br.com.dgs.notificacoes.application.usecases;

public interface SendNotificationUseCase {
    void sendNotification(ConsultaInput consulta, String topico);
}

