package br.com.dgs.autenticacao.application.services;

import br.com.dgs.autenticacao.infrastructure.security.adapters.TokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ServiceAuthenticationService {

    private final TokenServiceImpl tokenService;
    private final Map<String, String> serviceCredentials;

    public ServiceAuthenticationService(
            TokenServiceImpl tokenService,
            @Value("${services.credentials.agendamento.id:agendamento-service}") String agendamentoId,
            @Value("${services.credentials.agendamento.secret:}") String agendamentoSecret,
            @Value("${services.credentials.historico.id:historico-service}") String historicoId,
            @Value("${services.credentials.historico.secret:}") String historicoSecret,
            @Value("${services.credentials.notificacoes.id:notificacoes-service}") String notificacoesId,
            @Value("${services.credentials.notificacoes.secret:}") String notificacoesSecret,
            @Value("${services.credentials.atendimento.id:atendimento-service}") String atendimentoId,
            @Value("${services.credentials.atendimento.secret:}") String atendimentoSecret,
            @Value("${services.credentials.exames.id:exames-service}") String examesId,
            @Value("${services.credentials.exames.secret:}") String examesSecret) {

        this.tokenService = tokenService;

        log.info("Configurando credenciais de serviço:");
        log.info("Agendamento ID: {}", agendamentoId);
        log.info("Historico ID: {}", historicoId);
        log.info("Notificacoes ID: {}", notificacoesId);
        log.info("Atendimento ID: {}", atendimentoId);
        log.info("Exames ID: {}", examesId);

        this.serviceCredentials = Map.ofEntries(
                Map.entry(agendamentoId, agendamentoSecret),
                Map.entry(historicoId, historicoSecret),
                Map.entry(notificacoesId, notificacoesSecret),
                Map.entry(atendimentoId, atendimentoSecret),
                Map.entry(examesId, examesSecret)
        );
    }

    public String authenticateService(String serviceId, String serviceSecret) {
        log.info("Tentando autenticar serviço: {}", serviceId);

        if (!serviceCredentials.containsKey(serviceId)) {
            log.error("Serviço não encontrado: {}", serviceId);
            throw new IllegalArgumentException("Serviço não encontrado: " + serviceId);
        }

        String expectedSecret = serviceCredentials.get(serviceId);
        if (!expectedSecret.equals(serviceSecret)) {
            log.error("Secret inválido para serviço: {}", serviceId);
            throw new IllegalArgumentException("Credenciais de serviço inválidas");
        }

        log.info("Serviço autenticado com sucesso: {}", serviceId);
        return tokenService.generateServiceToken(serviceId);
    }
}
