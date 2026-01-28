package br.com.dgs.autenticacao.infrastructure.persistence.adapters;

import br.com.dgs.autenticacao.application.ports.outbound.PacienteRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;
import br.com.dgs.autenticacao.domain.paciente.model.Endereco;
import br.com.dgs.autenticacao.domain.paciente.model.Paciente;
import br.com.dgs.autenticacao.domain.paciente.model.PacienteId;
import br.com.dgs.autenticacao.domain.shared.model.Email;
import br.com.dgs.autenticacao.domain.shared.model.Senha;
import br.com.dgs.autenticacao.infrastructure.persistence.entity.PacienteEntity;
import br.com.dgs.autenticacao.infrastructure.persistence.repository.PacienteJPARepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PacienteRepositoryImpl implements PacienteRepository {

    private final PacienteJPARepository jpaRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteRepositoryImpl(PacienteJPARepository jpaRepository, PasswordEncoder passwordEncoder) {
        this.jpaRepository = jpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Paciente save(Paciente paciente) {
        PacienteEntity entity = toEntity(paciente);
        PacienteEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Paciente> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<Paciente> findById(PacienteId id) {
        return jpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    private PacienteEntity toEntity(Paciente paciente) {
        PacienteEntity entity = new PacienteEntity();

        if (paciente.getId() != null) {
            entity.setId(paciente.getId().getValue());
        }

        entity.setEmail(paciente.getEmail().getValue());
        entity.setNomeCompleto(paciente.getNomeCompleto());
        entity.setCpf(paciente.getCpf());
        entity.setDataNascimento(paciente.getDataNascimento());
        entity.setTelefone(paciente.getTelefone());
        entity.setAtivo(paciente.isAtivo());

        entity.setLogradouro(paciente.getEndereco().getLogradouro());
        entity.setNumero(paciente.getEndereco().getNumero());
        entity.setComplemento(paciente.getEndereco().getComplemento());
        entity.setBairro(paciente.getEndereco().getBairro());
        entity.setCidade(paciente.getEndereco().getCidade());
        entity.setEstado(paciente.getEndereco().getEstado());
        entity.setCep(paciente.getEndereco().getCep());

        String senhaValue = paciente.getSenha().getValue();
        if (isPasswordEncoded(senhaValue)) {
            entity.setSenha(senhaValue);
        } else {
            entity.setSenha(passwordEncoder.encode(paciente.getSenha()));
        }

        return entity;
    }

    private Paciente toDomain(PacienteEntity entity) {
        Email email = new Email(entity.getEmail());
        Senha senha = createSenhaFromEncoded(entity.getSenha());

        Endereco endereco = new Endereco(
                entity.getLogradouro(),
                entity.getNumero(),
                entity.getComplemento(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getEstado(),
                entity.getCep()
        );

        if (entity.getId() != null) {
            PacienteId id = new PacienteId(entity.getId());
            return new Paciente(
                    id, email, senha, entity.getNomeCompleto(), entity.getCpf(),
                    entity.getDataNascimento(), entity.getTelefone(), endereco, entity.isAtivo()
            );
        }

        return new Paciente(
                email, senha, entity.getNomeCompleto(), entity.getCpf(),
                entity.getDataNascimento(), entity.getTelefone(), endereco
        );
    }

    private boolean isPasswordEncoded(String password) {
        return password != null && password.startsWith("$2a$") && password.length() == 60;
    }

    private Senha createSenhaFromEncoded(String encodedPassword) {
        return new SenhaEncoded(encodedPassword);
    }

    private static class SenhaEncoded extends Senha {
        private final String encodedValue;

        public SenhaEncoded(String encodedValue) {
            super("temp123");
            this.encodedValue = encodedValue;
        }

        @Override
        public String getValue() {
            return encodedValue;
        }
    }
}
