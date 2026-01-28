package br.com.dgs.autenticacao.infrastructure.configuration;

import br.com.dgs.autenticacao.application.ports.inbound.AutenticacaoUseCase;
import br.com.dgs.autenticacao.application.ports.inbound.FuncionarioUseCase;
import br.com.dgs.autenticacao.application.ports.inbound.PacienteUseCase;
import br.com.dgs.autenticacao.application.ports.outbound.FuncionarioRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PacienteRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;
import br.com.dgs.autenticacao.application.ports.outbound.TokenService;
import br.com.dgs.autenticacao.application.services.AutenticacaoUseCaseImpl;
import br.com.dgs.autenticacao.application.services.FuncionarioUseCaseImpl;
import br.com.dgs.autenticacao.application.services.PacienteUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public PacienteUseCase pacienteUseCase(
            PacienteRepository pacienteRepository,
            PasswordEncoder passwordEncoder) {
        return new PacienteUseCaseImpl(pacienteRepository, passwordEncoder);
    }

    @Bean
    public FuncionarioUseCase funcionarioUseCase(
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder) {
        return new FuncionarioUseCaseImpl(funcionarioRepository, passwordEncoder);
    }

    @Bean
    public AutenticacaoUseCase autenticacaoUseCase(
            PacienteRepository pacienteRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        return new AutenticacaoUseCaseImpl(
                pacienteRepository, funcionarioRepository, passwordEncoder, tokenService);
    }
}
