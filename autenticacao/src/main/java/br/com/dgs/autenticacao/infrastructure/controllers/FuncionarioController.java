package br.com.dgs.autenticacao.infrastructure.controllers;

import br.com.dgs.autenticacao.application.dto.CadastrarFuncionarioCommand;
import br.com.dgs.autenticacao.application.dto.FuncionarioOutput;
import br.com.dgs.autenticacao.application.ports.inbound.FuncionarioUseCase;
import br.com.dgs.autenticacao.infrastructure.controllers.dto.FuncionarioRequestDTO;
import br.com.dgs.autenticacao.infrastructure.controllers.dto.FuncionarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioUseCase funcionarioUseCase;

    public FuncionarioController(FuncionarioUseCase funcionarioUseCase) {
        this.funcionarioUseCase = funcionarioUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(@RequestBody @Valid FuncionarioRequestDTO request) {
        CadastrarFuncionarioCommand command = request.toCommand();
        FuncionarioOutput output = funcionarioUseCase.cadastrarFuncionario(command);
        FuncionarioResponseDTO response = FuncionarioResponseDTO.fromOutput(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        FuncionarioOutput output = funcionarioUseCase.buscarPorId(id);
        FuncionarioResponseDTO response = FuncionarioResponseDTO.fromOutput(output);

        return ResponseEntity.ok(response);
    }
}
