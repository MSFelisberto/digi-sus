package br.com.dgs.autenticacao.application.services;

import br.com.dgs.autenticacao.application.dto.AutenticarCommand;
import br.com.dgs.autenticacao.application.dto.AuthTokenOutput;
import br.com.dgs.autenticacao.application.ports.inbound.AutenticacaoUseCase;
import br.com.dgs.autenticacao.application.ports.outbound.FuncionarioRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PacienteRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;
import br.com.dgs.autenticacao.application.ports.outbound.TokenService;
import org.springframework.security.authentication.BadCredentialsException;

public class AutenticacaoUseCaseImpl implements AutenticacaoUseCase {

    private final PacienteRepository pacienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticacaoUseCaseImpl(
            PacienteRepository pacienteRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.pacienteRepository = pacienteRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public AuthTokenOutput autenticar(AutenticarCommand command) {
        Email email = new Email(command.email());
        Senha senhaRaw = new Senha(command.senha());

        var pacienteOpt = pacienteRepository.findByEmail(email);
        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            if (passwordEncoder.matches(senhaRaw, paciente.getSenha().getValue())) {
                String token = tokenService.generateTokenForPaciente(paciente);
                return new AuthTokenOutput(token, "Bearer", 86400000L, "PACIENTE");
            }
        }

        var funcionarioOpt = funcionarioRepository.findByEmail(email);
        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();
            if (passwordEncoder.matches(senhaRaw, funcionario.getSenha().getValue())) {
                String token = tokenService.generateTokenForFuncionario(funcionario);
                return new AuthTokenOutput(token, "Bearer", 86400000L, funcionario.getTipo().name());
            }
        }

        throw new BadCredentialsException("Credenciais inválidas");
    }
}
