package br.com.dgs.autenticacao.infrastructure.security;

import br.com.dgs.autenticacao.application.ports.outbound.FuncionarioRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PacienteRepository;
import br.com.dgs.autenticacao.domain.funcionario.model.Funcionario;
import br.com.dgs.autenticacao.domain.paciente.model.Paciente;
import br.com.dgs.autenticacao.domain.shared.model.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PacienteRepository pacienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public UserDetailsServiceImpl(PacienteRepository pacienteRepository,
                                  FuncionarioRepository funcionarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrServiceId) throws UsernameNotFoundException {
        log.debug("Tentando carregar usuário: {}", emailOrServiceId);

        if (emailOrServiceId.endsWith("-service")) {
            log.debug("Identificado como serviceId: {}", emailOrServiceId);
            return new CustomUserDetails(
                    0L,
                    emailOrServiceId,
                    "N/A",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_SISTEMA"))
            );
        }

        try {
            Email emailVO = new Email(emailOrServiceId);

            Optional<Paciente> pacienteOpt = pacienteRepository.findByEmail(emailVO);
            if (pacienteOpt.isPresent()) {
                Paciente paciente = pacienteOpt.get();
                log.debug("Paciente encontrado: {}", paciente.getId().getValue());
                return new CustomUserDetails(
                        paciente.getId().getValue(),
                        paciente.getEmail().getValue(),
                        paciente.getSenha().getValue(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_PACIENTE"))
                );
            }

            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByEmail(emailVO);
            if (funcionarioOpt.isPresent()) {
                Funcionario funcionario = funcionarioOpt.get();
                log.debug("Funcionário encontrado: {} - Tipo: {}", funcionario.getId().getValue(), funcionario.getTipo());
                return new CustomUserDetails(
                        funcionario.getId().getValue(),
                        funcionario.getEmail().getValue(),
                        funcionario.getSenha().getValue(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + funcionario.getTipo().name()))
                );
            }
        } catch (Exception e) {
            log.error("Erro ao processar email: {}", e.getMessage());
        }

        log.warn("Usuário não encontrado: {}", emailOrServiceId);
        throw new UsernameNotFoundException("Usuário não encontrado: " + emailOrServiceId);
    }
}