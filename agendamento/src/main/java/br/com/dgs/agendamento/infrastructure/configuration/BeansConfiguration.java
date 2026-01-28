package br.com.dgs.agendamento.infrastructure.configuration;

import br.com.dgs.agendamento.application.ports.inbound.AgendamentoUseCase;
import br.com.dgs.agendamento.application.ports.outbound.ConsultaRepository;
import br.com.dgs.agendamento.application.ports.outbound.NotificationService;
import br.com.dgs.agendamento.application.ports.outbound.PacienteService;
import br.com.dgs.agendamento.application.services.AgendamentoUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public AgendamentoUseCase agendamentoUseCase(
            ConsultaRepository consultaRepository,
            PacienteService pacienteService,
            NotificationService notificationService) {

        return new AgendamentoUseCaseImpl(
                consultaRepository,
                pacienteService,
                notificationService
        );
    }
}