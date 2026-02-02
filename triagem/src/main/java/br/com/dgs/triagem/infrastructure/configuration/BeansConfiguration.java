package br.com.dgs.triagem.infrastructure.configuration;

import br.com.dgs.triagem.application.ports.inbound.TriagemUseCase;
import br.com.dgs.triagem.application.ports.outbound.FuncionarioService;
import br.com.dgs.triagem.application.ports.outbound.PacienteService;
import br.com.dgs.triagem.application.ports.outbound.TriagemMessagingService;
import br.com.dgs.triagem.application.services.TriagemUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public TriagemUseCase triagemUseCase(TriagemMessagingService messagingService,
                                         PacienteService pacienteService,
                                         FuncionarioService funcionarioService) {
        return new TriagemUseCaseImpl(messagingService, pacienteService, funcionarioService);
    }
}
