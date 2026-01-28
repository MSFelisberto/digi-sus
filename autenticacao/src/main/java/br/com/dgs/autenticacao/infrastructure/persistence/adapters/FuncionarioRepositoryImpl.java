package br.com.dgs.autenticacao.infrastructure.persistence.adapters;

import br.com.dgs.autenticacao.application.ports.outbound.FuncionarioRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;
import br.com.dgs.autenticacao.domain.funcionario.model.Especialidade;
import br.com.dgs.autenticacao.domain.funcionario.model.Funcionario;
import br.com.dgs.autenticacao.domain.funcionario.model.FuncionarioId;
import br.com.dgs.autenticacao.domain.shared.model.Email;
import br.com.dgs.autenticacao.domain.shared.model.Senha;
import br.com.dgs.autenticacao.infrastructure.persistence.entity.FuncionarioEntity;
import br.com.dgs.autenticacao.infrastructure.persistence.repository.FuncionarioJPARepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final FuncionarioJPARepository jpaRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioRepositoryImpl(FuncionarioJPARepository jpaRepository, PasswordEncoder passwordEncoder) {
        this.jpaRepository = jpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        FuncionarioEntity entity = toEntity(funcionario);
        FuncionarioEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Funcionario> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<Funcionario> findById(FuncionarioId id) {
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

    @Override
    public boolean existsByCrm(String crm) {
        return jpaRepository.existsByCrm(crm);
    }

    @Override
    public boolean existsByCoren(String coren) {
        return jpaRepository.existsByCoren(coren);
    }

    private FuncionarioEntity toEntity(Funcionario funcionario) {
        FuncionarioEntity entity = new FuncionarioEntity();

        if (funcionario.getId() != null) {
            entity.setId(funcionario.getId().getValue());
        }

        entity.setEmail(funcionario.getEmail().getValue());
        entity.setTipo(funcionario.getTipo());
        entity.setNomeCompleto(funcionario.getNomeCompleto());
        entity.setCpf(funcionario.getCpf());
        entity.setCrm(funcionario.getCrm());
        entity.setCoren(funcionario.getCoren());
        entity.setAtivo(funcionario.isAtivo());
        entity.setDataCadastro(funcionario.getDataCadastro());

        if (funcionario.getEspecialidade() != null) {
            entity.setEspecialidadeNome(funcionario.getEspecialidade().getNome());
            entity.setEspecialidadeCodigo(funcionario.getEspecialidade().getCodigo());
        }

        String senhaValue = funcionario.getSenha().getValue();
        if (isPasswordEncoded(senhaValue)) {
            entity.setSenha(senhaValue);
        } else {
            entity.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        }

        return entity;
    }

    private Funcionario toDomain(FuncionarioEntity entity) {
        Email email = new Email(entity.getEmail());
        Senha senha = createSenhaFromEncoded(entity.getSenha());

        Especialidade especialidade = null;
        if (entity.getEspecialidadeNome() != null && entity.getEspecialidadeCodigo() != null) {
            especialidade = new Especialidade(entity.getEspecialidadeNome(), entity.getEspecialidadeCodigo());
        }

        if (entity.getId() != null) {
            FuncionarioId id = new FuncionarioId(entity.getId());
            return new Funcionario(
                    id, email, senha, entity.getTipo(), entity.getNomeCompleto(), entity.getCpf(),
                    entity.getCrm(), entity.getCoren(), especialidade, entity.isAtivo(), entity.getDataCadastro()
            );
        }

        return new Funcionario(
                email, senha, entity.getTipo(), entity.getNomeCompleto(), entity.getCpf(),
                entity.getCrm(), entity.getCoren(), especialidade
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
