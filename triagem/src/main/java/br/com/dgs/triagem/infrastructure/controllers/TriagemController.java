package br.com.dgs.triagem.infrastructure.controllers;

import br.com.dgs.triagem.application.dto.AuthenticatedUser;
import br.com.dgs.triagem.application.dto.RealizarTriagemCommand;
import br.com.dgs.triagem.application.dto.TriagemOutput;
import br.com.dgs.triagem.application.ports.inbound.TriagemUseCase;
import br.com.dgs.triagem.infrastructure.controllers.dto.TriagemRequestDTO;
import br.com.dgs.triagem.infrastructure.controllers.dto.TriagemResponseDTO;
import br.com.dgs.triagem.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/triagem")
public class TriagemController {

    private final TriagemUseCase triagemUseCase;

    public TriagemController(TriagemUseCase triagemUseCase) {
        this.triagemUseCase = triagemUseCase;
    }

    @PostMapping
    public ResponseEntity<TriagemResponseDTO> realizarTriagem(
            @Valid @RequestBody TriagemRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.info("Recebida requisição de triagem para paciente ID: {}", request.pacienteId());

        AuthenticatedUser user = new AuthenticatedUser(
                principal.getUserId(),
                principal.getEmail(),
                principal.getRoles()
        );

        RealizarTriagemCommand command = new RealizarTriagemCommand(
                request.pacienteId(),
                request.funcionarioId(),
                request.pressaoArterial(),
                request.temperatura(),
                request.batimentoCardiaco(),
                request.conduta(),
                request.especialidade()
        );

        TriagemOutput output = triagemUseCase.realizarTriagem(command, user);

        TriagemResponseDTO response = new TriagemResponseDTO(
                output.triagemId(),
                output.pacienteId(),
                output.funcionarioId(),
                output.pressaoArterial(),
                output.temperatura(),
                output.batimentoCardiaco(),
                output.conduta(),
                output.especialidade(),
                output.prioridade(),
                output.mensagem()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
