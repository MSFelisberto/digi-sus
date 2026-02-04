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
    public TipoExameUseCase tipoExameUseCase(TipoExameRepository tipoExameRepository) {
        return new TipoExameUseCaseImpl(tipoExameRepository);
    }

    @Bean
    public SolicitacaoExameUseCase solicitacaoExameUseCase(
            SolicitacaoExameRepository solicitacaoExameRepository,
            TipoExameRepository tipoExameRepository,
            PacienteService pacienteService,
            ExameNotificationService exameNotificationService) {

        return new SolicitacaoExameUseCaseImpl(
                solicitacaoExameRepository,
                tipoExameRepository,
                pacienteService,
                exameNotificationService
        );
    }

    @Bean
    public AgendamentoExameUseCase agendamentoExameUseCase(
            AgendamentoExameRepository agendamentoExameRepository,
            SolicitacaoExameRepository solicitacaoExameRepository,
            AgendaExameRepository agendaExameRepository,
            TipoExameRepository tipoExameRepository,
            ExameNotificationService exameNotificationService) {

        return new AgendamentoExameUseCaseImpl(
                agendamentoExameRepository,
                solicitacaoExameRepository,
                agendaExameRepository,
                tipoExameRepository,
                exameNotificationService
        );
    }
}