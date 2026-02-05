package br.com.dgs.exames.infrastructure.configuration;

import br.com.dgs.exames.application.ports.inbound.AgendamentoExameUseCase;
import br.com.dgs.exames.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.exames.application.ports.inbound.TipoExameUseCase;
import br.com.dgs.exames.application.ports.outbound.*;
import br.com.dgs.exames.application.services.AgendamentoExameUseCaseImpl;
import br.com.dgs.exames.application.services.SolicitacaoExameUseCaseImpl;
import br.com.dgs.exames.application.services.TipoExameUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public TipoExameUseCase tipoExameUseCase(TipoExameRepository tipoExameRepository) {
        return new TipoExameUseCaseImpl(tipoExameRepository);
    }

    @Bean
    public SolicitacaoExameUseCase solicitacaoExameUseCase(
            SolicitacaoExameRepository solicitacaoRepository,
            TipoExameRepository tipoExameRepository,
            PacienteService pacienteService,
            ExameNotificationService exameNotificationService) {

        return new SolicitacaoExameUseCaseImpl(
                solicitacaoRepository,
                tipoExameRepository,
                pacienteService,
                exameNotificationService
        );
    }

    @Bean
    public AgendamentoExameUseCase agendamentoExameUseCase(
            AgendamentoExameRepository agendamentoRepository,
            SolicitacaoExameRepository solicitacaoRepository,
            AgendaExameRepository agendaExameRepository,
            TipoExameRepository tipoExameRepository,
            ExameNotificationService exameNotificationService) {

        return new AgendamentoExameUseCaseImpl(
                agendamentoRepository,
                solicitacaoRepository,
                agendaExameRepository,
                tipoExameRepository,
                exameNotificationService
        );
    }
}
