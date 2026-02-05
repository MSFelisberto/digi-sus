package br.com.dgs.atendimento.infrastructure.configuration;

import br.com.dgs.atendimento.application.ports.inbound.AtendimentoUseCase;
import br.com.dgs.atendimento.application.ports.outbound.AtendimentoEventPublisher;
import br.com.dgs.atendimento.application.ports.outbound.AtendimentoRepository;
import br.com.dgs.atendimento.application.ports.outbound.ConsultaService;
import br.com.dgs.atendimento.application.services.AtendimentoUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public AtendimentoUseCase atendimentoUseCase(
            AtendimentoRepository atendimentoRepository,
            ConsultaService consultaService,
            AtendimentoEventPublisher eventPublisher) {

        return new AtendimentoUseCaseImpl(
                atendimentoRepository,
                consultaService,
                eventPublisher
        );
    }
}
