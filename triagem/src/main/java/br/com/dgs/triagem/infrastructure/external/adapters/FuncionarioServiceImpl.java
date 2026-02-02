package br.com.dgs.triagem.infrastructure.external.adapters;

import br.com.dgs.triagem.application.ports.outbound.FuncionarioService;
import br.com.dgs.triagem.infrastructure.external.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class FuncionarioServiceImpl implements FuncionarioService {

    private final RestTemplate restTemplate;
    private final String funcionarioServiceUrl;
    private final AuthenticationService authenticationService;

    public FuncionarioServiceImpl(
            RestTemplate restTemplate,
            @Value("${services.autenticacao.url}") String funcionarioServiceUrl,
            AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.funcionarioServiceUrl = funcionarioServiceUrl;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean existeFuncionario(Long funcionarioId) {
        try {
            String serviceToken = authenticationService.getServiceToken();

            String url = funcionarioServiceUrl + "/funcionarios/" + funcionarioId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException e) {
            log.warn("Funcionário com ID {} não encontrado: {}", funcionarioId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar existência do funcionário {}: {}", funcionarioId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEnfermeiro(Long funcionarioId) {
        try {
            String serviceToken = authenticationService.getServiceToken();

            String url = funcionarioServiceUrl + "/funcionarios/" + funcionarioId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String tipo = (String) response.getBody().get("tipo");
                return "ENFERMEIRO".equals(tipo);
            }

            return false;

        } catch (Exception e) {
            log.error("Erro ao verificar tipo do funcionário {}: {}", funcionarioId, e.getMessage());
            return false;
        }
    }
}
