package br.com.dgs.exames.infrastructure.external.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class AuthenticationService {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;
    private final String serviceId;
    private final String serviceSecret;

    private String cachedToken;
    private long tokenExpirationTime;

    public AuthenticationService(
            RestTemplate restTemplate,
            @Value("${services.autenticacao.url}") String authServiceUrl,
            @Value("${service.id}") String serviceId,
            @Value("${service.secret}") String serviceSecret) {

        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
        this.serviceId = serviceId;
        this.serviceSecret = serviceSecret;

        log.info("AuthenticationService inicializado:");
        log.info("Auth Service URL: {}", authServiceUrl);
        log.info("Service ID: {}", serviceId);
        log.info("Service Secret configurado: {}", serviceSecret != null && !serviceSecret.isEmpty());
    }

    public String getServiceToken() {
        if (isTokenValid()) {
            log.debug("Usando token em cache");
            return cachedToken;
        }

        log.info("Token expirado ou não existe, obtendo novo token");
        return authenticateService();
    }

    private boolean isTokenValid() {
        return cachedToken != null && System.currentTimeMillis() < tokenExpirationTime;
    }

    private String authenticateService() {
        try {
            String url = authServiceUrl + "/auth/service/token";
            log.info("Fazendo chamada para: {}", url);

            Map<String, String> request = Map.of(
                    "serviceId", serviceId,
                    "serviceSecret", serviceSecret
            );

            log.info("Payload da requisição: serviceId={}", serviceId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            log.info("Status da resposta: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                cachedToken = (String) responseBody.get("token");

                Object expiresInObj = responseBody.get("expiresIn");
                Long expiresIn = null;
                if (expiresInObj instanceof Integer) {
                    expiresIn = ((Integer) expiresInObj).longValue();
                } else if (expiresInObj instanceof Long) {
                    expiresIn = (Long) expiresInObj;
                }

                if (expiresIn != null) {
                    tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 60000);
                }

                log.info("Token de serviço obtido com sucesso");
                return cachedToken;
            }

            throw new RuntimeException("Falha na autenticação do serviço - Status: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("Erro ao obter token de serviço: {}", e.getMessage());
            throw new RuntimeException("Não foi possível autenticar o serviço", e);
        }
    }
}
