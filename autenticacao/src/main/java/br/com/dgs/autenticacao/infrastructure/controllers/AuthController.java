package br.com.dgs.autenticacao.infrastructure.controllers;

import br.com.dgs.autenticacao.application.dto.AutenticarCommand;
import br.com.dgs.autenticacao.application.dto.AuthTokenOutput;
import br.com.dgs.autenticacao.application.ports.inbound.AutenticacaoUseCase;
import br.com.dgs.autenticacao.infrastructure.controllers.dto.AuthRequestDTO;
import br.com.dgs.autenticacao.infrastructure.controllers.dto.AuthResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticacaoUseCase autenticacaoUseCase;

    public AuthController(AutenticacaoUseCase autenticacaoUseCase) {
        this.autenticacaoUseCase = autenticacaoUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO request) {
        AutenticarCommand command = new AutenticarCommand(
                request.email(),
                request.senha()
        );

        AuthTokenOutput output = autenticacaoUseCase.autenticar(command);

        AuthResponseDTO response = new AuthResponseDTO(
                output.token(),
                output.type(),
                output.expiresIn(),
                output.userType()
        );

        return ResponseEntity.ok(response);
    }
}
