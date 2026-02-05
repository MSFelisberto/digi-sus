package br.com.dgs.exames.infrastructure.external.adapters;

import br.com.dgs.exames.application.ports.outbound.PacienteService;
import br.com.dgs.exames.domain.model.PacienteId;
import br.com.dgs.exames.infrastructure.external.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PacienteServiceImpl implements PacienteService {

    private final RestTemplate restTemplate;
    private final String pacienteServiceUrl;
    private final AuthenticationService authenticationService;

    public PacienteServiceImpl(
            RestTemplate restTemplate,
            @Value("${services.autenticacao.url}") String pacienteServiceUrl,
            AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.pacienteServiceUrl = pacienteServiceUrl;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean exists(PacienteId pacienteId) {
        try {
            String serviceToken = authenticationService.getServiceToken();

            String url = pacienteServiceUrl + "/internal/usuarios/pacientes/" +
                    pacienteId.getValue() + "/exists";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Boolean.class);

            return Boolean.TRUE.equals(response.getBody());

        } catch (HttpClientErrorException e) {
            log.warn("Paciente com ID {} não encontrado: {}", pacienteId.getValue(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar existência do paciente {}: {}",
                    pacienteId.getValue(), e.getMessage());
            return false;
        }
    }
}
