package br.com.dgs.agendamento.infrastructure.configuration;

import br.com.dgs.agendamento.application.ports.inbound.*;
import br.com.dgs.agendamento.application.ports.outbound.*;
import br.com.dgs.agendamento.application.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public AgendamentoUseCase agendamentoUseCase(
            ConsultaRepository consultaRepository,
            PacienteService pacienteService,
            NotificationService notificationService,
            HorarioDisponivelRepository horarioDisponivelRepository) {

        return new AgendamentoUseCaseImpl(
                consultaRepository,
                pacienteService,
                notificationService,
                horarioDisponivelRepository
        );
    }

    @Bean
    public AgendaUseCase agendaUseCase(
            AgendaRepository agendaRepository,
            HorarioDisponivelRepository horarioDisponivelRepository,
            FuncionarioService funcionarioService) {

        return new AgendaUseCaseImpl(agendaRepository, horarioDisponivelRepository, funcionarioService);
    }

    @Bean
    public HorarioDisponivelUseCase horarioDisponivelUseCase(
            HorarioDisponivelRepository horarioDisponivelRepository,
            ConsultaRepository consultaRepository,
            PacienteService pacienteService,
            NotificationService notificationService) {

        return new HorarioDisponivelUseCaseImpl(
                horarioDisponivelRepository,
                consultaRepository,
                pacienteService,
                notificationService
        );
    }

    @Bean
    public ConsultaTriagemUseCase consultaTriagemUseCase(
            ConsultaRepository consultaRepository,
            HorarioDisponivelRepository horarioDisponivelRepository,
            NotificationService notificationService) {

        return new ConsultaTriagemUseCaseImpl(
                consultaRepository,
                horarioDisponivelRepository,
                notificationService
        );
    }

    @Bean
    public AgendamentoExameUseCase agendamentoExameUseCase(
            AgendaExameRepository agendaExameRepository,
            HorarioExameDisponivelRepository horarioExameDisponivelRepository,
            AgendamentoExameRepository agendamentoExameRepository,
            ExameEventPublisher exameEventPublisher) {

        return new AgendamentoExameUseCaseImpl(
                agendaExameRepository,
                horarioExameDisponivelRepository,
                agendamentoExameRepository,
                exameEventPublisher
        );
    }
}
