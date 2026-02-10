package br.com.dgs.atendimento.infrastructure.external.adapters;

import br.com.dgs.atendimento.application.ports.outbound.ConsultaService;
import br.com.dgs.atendimento.domain.exception.ConsultaInvalidaException;
import br.com.dgs.atendimento.domain.model.ConsultaId;
import br.com.dgs.atendimento.infrastructure.external.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class ConsultaServiceImpl implements ConsultaService {

    private final RestTemplate restTemplate;
    private final String agendamentoServiceUrl;
    private final AuthenticationService authenticationService;

    public ConsultaServiceImpl(
            RestTemplate restTemplate,
            @Value("${services.agendamento.url}") String agendamentoServiceUrl,
            AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.agendamentoServiceUrl = agendamentoServiceUrl;
        this.authenticationService = authenticationService;
    }

    @Override
    public ConsultaInfo buscarConsulta(ConsultaId consultaId) {
        try {
            String serviceToken = authenticationService.getServiceToken();
            String url = agendamentoServiceUrl + "/internal/consultas/" + consultaId.getValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return new ConsultaInfo(
                        ((Number) body.get("id")).longValue(),
                        ((Number) body.get("pacienteId")).longValue(),
                        ((Number) body.get("medicoId")).longValue(),
                        (String) body.get("status")
                );
            }

            throw new ConsultaInvalidaException("Consulta não encontrada com ID: " + consultaId.getValue());

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Consulta não encontrada: {}", consultaId.getValue());
            throw new ConsultaInvalidaException("Consulta não encontrada com ID: " + consultaId.getValue());
        } catch (ConsultaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar consulta {}: {}", consultaId.getValue(), e.getMessage());
            throw new RuntimeException("Erro ao buscar dados da consulta", e);
        }
    }

    @Override
    public void marcarComoRealizada(ConsultaId consultaId) {
        try {
            String serviceToken = authenticationService.getServiceToken();
            String url = agendamentoServiceUrl + "/internal/consultas/" + consultaId.getValue() + "/realizada";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);

            log.info("Consulta {} marcada como realizada", consultaId.getValue());

        } catch (Exception e) {
            log.error("Erro ao marcar consulta {} como realizada: {}", consultaId.getValue(), e.getMessage());
            throw new RuntimeException("Erro ao marcar consulta como realizada", e);
        }
    }

    @Override
    public void marcarComoEmAtendimento(ConsultaId consultaId) {
        try {
            String serviceToken = authenticationService.getServiceToken();
            String url = agendamentoServiceUrl + "/internal/consultas/" + consultaId.getValue() + "/em-atendimento";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);

            log.info("Consulta {} marcada como em atendimento", consultaId.getValue());

        } catch (Exception e) {
            log.error("Erro ao marcar consulta {} como em atendimento: {}", consultaId.getValue(), e.getMessage());
            throw new RuntimeException("Erro ao marcar consulta como em atendimento", e);
        }
    }
}
