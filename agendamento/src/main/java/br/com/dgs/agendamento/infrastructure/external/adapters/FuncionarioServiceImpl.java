package br.com.dgs.agendamento.infrastructure.external.adapters;

import br.com.dgs.agendamento.application.ports.outbound.FuncionarioService;
import br.com.dgs.agendamento.domain.model.MedicoId;
import br.com.dgs.agendamento.infrastructure.external.service.AuthenticationService;
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
public class FuncionarioServiceImpl implements FuncionarioService {

    private final RestTemplate restTemplate;
    private final String autenticacaoServiceUrl;
    private final AuthenticationService authenticationService;

    public FuncionarioServiceImpl(
            RestTemplate restTemplate,
            @Value("${services.autenticacao.url}") String autenticacaoServiceUrl,
            AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.autenticacaoServiceUrl = autenticacaoServiceUrl;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean isMedico(MedicoId funcionarioId) {
        try {
            String serviceToken = authenticationService.getServiceToken();

            String url = autenticacaoServiceUrl + "/internal/usuarios/funcionarios/" +
                    funcionarioId.getValue() + "/is-medico";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Boolean.class);

            return Boolean.TRUE.equals(response.getBody());

        } catch (HttpClientErrorException e) {
            log.warn("Funcionario com ID {} nao encontrado: {}", funcionarioId.getValue(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar se funcionario {} eh medico: {}",
                    funcionarioId.getValue(), e.getMessage());
            return false;
        }
    }
}
