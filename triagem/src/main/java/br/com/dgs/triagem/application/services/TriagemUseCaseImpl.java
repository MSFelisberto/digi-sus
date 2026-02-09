package br.com.dgs.triagem.application.services;

import br.com.dgs.triagem.application.dto.AuthenticatedUser;
import br.com.dgs.triagem.application.dto.RealizarTriagemCommand;
import br.com.dgs.triagem.application.dto.TriagemOutput;
import br.com.dgs.triagem.application.ports.inbound.TriagemUseCase;
import br.com.dgs.triagem.application.ports.outbound.FuncionarioService;
import br.com.dgs.triagem.application.ports.outbound.PacienteService;
import br.com.dgs.triagem.application.ports.outbound.TriagemMessagingService;
import br.com.dgs.triagem.domain.exception.AuthorizationException;
import br.com.dgs.triagem.domain.exception.FuncionarioNotFoundException;
import br.com.dgs.triagem.domain.exception.PacienteNotFoundException;
import br.com.dgs.triagem.domain.model.FuncionarioId;
import br.com.dgs.triagem.domain.model.PacienteId;
import br.com.dgs.triagem.domain.model.Triagem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriagemUseCaseImpl implements TriagemUseCase {

    private final TriagemMessagingService messagingService;
    private final PacienteService pacienteService;
    private final FuncionarioService funcionarioService;

    public TriagemUseCaseImpl(TriagemMessagingService messagingService,
                              PacienteService pacienteService,
                              FuncionarioService funcionarioService) {
        this.messagingService = messagingService;
        this.pacienteService = pacienteService;
        this.funcionarioService = funcionarioService;
    }

    @Override
    public TriagemOutput realizarTriagem(RealizarTriagemCommand command, AuthenticatedUser user) {
        log.info("Iniciando triagem para paciente ID: {}", command.pacienteId());

        validarAutorizacao(user);
        validarFuncionario(command.funcionarioId(), user);
        validarPaciente(command.pacienteId());

        Triagem triagem = new Triagem(
                new PacienteId(command.pacienteId()),
                new FuncionarioId(command.funcionarioId()),
                command.pressaoArterial(),
                command.temperatura(),
                command.batimentoCardiaco(),
                command.conduta(),
                command.especialidade()
        );

        messagingService.enviarParaAtendimento(triagem);
        messagingService.enviarParaHistorico(triagem);

        log.info("Triagem realizada com sucesso. ID: {}", triagem.getId().getValue());

        return new TriagemOutput(
                triagem.getId().getValue(),
                triagem.getPacienteId().getValue(),
                triagem.getFuncionarioId().getValue(),
                triagem.getPressaoArterial(),
                triagem.getTemperatura(),
                triagem.getBatimentoCardiaco(),
                triagem.getConduta(),
                triagem.getEspecialidade(),
                triagem.getPrioridade().name(),
                "Triagem realizada com sucesso"
        );
    }

    private void validarAutorizacao(AuthenticatedUser user) {
        if (!user.roles().contains("ROLE_ENFERMEIRO")) {
            throw new AuthorizationException("Apenas enfermeiros podem realizar triagem");
        }
    }

    private void validarFuncionario(Long funcionarioId, AuthenticatedUser user) {
        if (!funcionarioService.existeFuncionario(funcionarioId)) {
            throw new FuncionarioNotFoundException("Funcionário não encontrado: " + funcionarioId);
        }

        if (!funcionarioService.isEnfermeiro(funcionarioId)) {
            throw new AuthorizationException("Funcionário não é enfermeiro");
        }

        if (!user.userId().equals(funcionarioId)) {
            throw new AuthorizationException("Funcionário autenticado não corresponde ao funcionário da triagem");
        }
    }

    private void validarPaciente(Long pacienteId) {
        if (!pacienteService.existePaciente(pacienteId)) {
            throw new PacienteNotFoundException("Paciente não encontrado: " + pacienteId);
        }
    }
}
