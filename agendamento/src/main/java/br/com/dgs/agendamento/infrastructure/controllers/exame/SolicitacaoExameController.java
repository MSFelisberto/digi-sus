package br.com.dgs.agendamento.infrastructure.controllers.exame;

import br.com.dgs.agendamento.application.dto.AuthenticatedUser;
import br.com.dgs.agendamento.application.dto.exame.CriarSolicitacaoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.ListarSolicitacoesQuery;
import br.com.dgs.agendamento.application.dto.exame.SolicitacaoExameOutput;
import br.com.dgs.agendamento.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.exame.SolicitacaoExameRequestDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.exame.SolicitacaoExameResponseDTO;
import br.com.dgs.agendamento.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.agendamento.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exames/solicitacoes")
public class SolicitacaoExameController {

    private final SolicitacaoExameUseCase solicitacaoExameUseCase;

    public SolicitacaoExameController(SolicitacaoExameUseCase solicitacaoExameUseCase) {
        this.solicitacaoExameUseCase = solicitacaoExameUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<SolicitacaoExameResponseDTO> criarSolicitacao(
            @RequestBody @Valid SolicitacaoExameRequestDTO requestDTO) {

        CriarSolicitacaoExameCommand command = new CriarSolicitacaoExameCommand(
                requestDTO.pacienteId(),
                requestDTO.medicoId(),
                requestDTO.tipoExameId(),
                requestDTO.prioridade(),
                requestDTO.observacoes()
        );

        SolicitacaoExameOutput output = solicitacaoExameUseCase.criarSolicitacao(command);
        SolicitacaoExameResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/agendamento/exames/solicitacoes/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'PACIENTE', 'ADMIN')")
    public ResponseEntity<List<SolicitacaoExameResponseDTO>> listarPorPaciente(
            @PathVariable Long pacienteId,
            Authentication authentication) {

        UserPrincipal principal = ((JwtAuthenticationToken) authentication).getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        AuthenticatedUser currentUser = new AuthenticatedUser(
                principal.getId(),
                principal.getEmail(),
                roles
        );

        ListarSolicitacoesQuery query = new ListarSolicitacoesQuery(pacienteId, currentUser);
        List<SolicitacaoExameResponseDTO> response = solicitacaoExameUseCase.listarPorPaciente(query)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<Void> cancelarSolicitacao(@PathVariable Long id) {
        solicitacaoExameUseCase.cancelarSolicitacao(id);
        return ResponseEntity.noContent().build();
    }

    private SolicitacaoExameResponseDTO toResponseDTO(SolicitacaoExameOutput output) {
        return new SolicitacaoExameResponseDTO(
                output.id(),
                output.pacienteId(),
                output.medicoId(),
                output.tipoExameId(),
                output.tipoExameNome(),
                output.prioridade(),
                output.observacoes(),
                output.status(),
                output.dataCriacao()
        );
    }
}