package br.com.dgs.autenticacao.application.services;

import br.com.dgs.autenticacao.application.dto.CadastrarFuncionarioCommand;
import br.com.dgs.autenticacao.application.dto.EspecialidadeDTO;
import br.com.dgs.autenticacao.application.dto.FuncionarioOutput;
import br.com.dgs.autenticacao.application.ports.inbound.FuncionarioUseCase;
import br.com.dgs.autenticacao.application.ports.outbound.FuncionarioRepository;
import br.com.dgs.autenticacao.application.ports.outbound.PasswordEncoder;

public class FuncionarioUseCaseImpl implements FuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioUseCaseImpl(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public FuncionarioOutput cadastrarFuncionario(CadastrarFuncionarioCommand command) {
        Email email = new Email(command.email());

        if (funcionarioRepository.existsByEmail(email)) {
            throw new FuncionarioBusinessException("Já existe um funcionário com este e-mail: " + email.getValue());
        }

        if (funcionarioRepository.existsByCpf(command.cpf())) {
            throw new FuncionarioBusinessException("Já existe um funcionário com este CPF");
        }

        if (command.crm() != null && !command.crm().trim().isEmpty()) {
            if (funcionarioRepository.existsByCrm(command.crm())) {
                throw new FuncionarioBusinessException("Já existe um médico com este CRM");
            }
        }

        if (command.coren() != null && !command.coren().trim().isEmpty()) {
            if (funcionarioRepository.existsByCoren(command.coren())) {
                throw new FuncionarioBusinessException("Já existe um enfermeiro com este COREN");
            }
        }

        Senha senha = new Senha(command.senha());
        Especialidade especialidade = null;

        if (command.especialidade() != null) {
            especialidade = new Especialidade(
                    command.especialidade().nome(),
                    command.especialidade().codigo()
            );
        }

        Funcionario funcionario = new Funcionario(
                email,
                senha,
                command.tipo(),
                command.nomeCompleto(),
                command.cpf(),
                command.crm(),
                command.coren(),
                especialidade
        );

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        return mapToOutput(funcionarioSalvo);
    }

    @Override
    public FuncionarioOutput buscarPorId(Long id) {
        FuncionarioId funcionarioId = new FuncionarioId(id);
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado com ID: " + id));

        return mapToOutput(funcionario);
    }

    private FuncionarioOutput mapToOutput(Funcionario funcionario) {
        EspecialidadeDTO especialidadeDTO = null;
        if (funcionario.getEspecialidade() != null) {
            especialidadeDTO = new EspecialidadeDTO(
                    funcionario.getEspecialidade().getNome(),
                    funcionario.getEspecialidade().getCodigo()
            );
        }

        return new FuncionarioOutput(
                funcionario.getId().getValue(),
                funcionario.getEmail().getValue(),
                funcionario.getTipo(),
                funcionario.getNomeCompleto(),
                funcionario.getCpf(),
                funcionario.getCrm(),
                funcionario.getCoren(),
                especialidadeDTO,
                funcionario.isAtivo(),
                funcionario.getDataCadastro()
        );
    }
}
