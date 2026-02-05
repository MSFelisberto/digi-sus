package br.com.dgs.exames.infrastructure.controllers;

import br.com.dgs.exames.application.dto.AuthenticatedUser;
import br.com.dgs.exames.application.dto.CriarSolicitacaoExameCommand;
import br.com.dgs.exames.application.dto.ListarSolicitacoesQuery;
import br.com.dgs.exames.application.dto.SolicitacaoExameOutput;
import br.com.dgs.exames.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.exames.infrastructure.controllers.dto.SolicitacaoExameRequestDTO;
import br.com.dgs.exames.infrastructure.controllers.dto.SolicitacaoExameResponseDTO;
import br.com.dgs.exames.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.exames.infrastructure.security.UserPrincipal;
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
@RequestMapping("/solicitacoes")
public class SolicitacaoExameController {

    private final SolicitacaoExameUseCase solicitacaoExameUseCase;

    public SolicitacaoExameController(SolicitacaoExameUseCase solicitacaoExameUseCase) {
        this.solicitacaoExameUseCase = solicitacaoExameUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'SISTEMA')")
    public ResponseEntity<SolicitacaoExameResponseDTO> criarSolicitacao(
            @RequestBody @Valid SolicitacaoExameRequestDTO requestDTO) {

        CriarSolicitacaoExameCommand command = new CriarSolicitacaoExameCommand(
                requestDTO.pacienteId(),
                requestDTO.medicoId(),
                requestDTO.tipoExameId(),
                requestDTO.atendimentoId(),
                requestDTO.consultaId(),
                requestDTO.prioridade() != null ? requestDTO.prioridade() : "NORMAL",
                requestDTO.observacoes()
        );

        SolicitacaoExameOutput output = solicitacaoExameUseCase.criarSolicitacao(command);
        SolicitacaoExameResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/exames/solicitacoes/" + response.id());
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

        ListarSolicitacoesQuery query = new ListarSolicitacoesQuery(pacienteId, null, currentUser);
        List<SolicitacaoExameResponseDTO> response = solicitacaoExameUseCase.listarPorPaciente(query)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/atendimento/{atendimentoId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public ResponseEntity<List<SolicitacaoExameResponseDTO>> listarPorAtendimento(
            @PathVariable Long atendimentoId) {

        List<SolicitacaoExameResponseDTO> response = solicitacaoExameUseCase.listarPorAtendimento(atendimentoId)
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
                output.atendimentoId(),
                output.consultaId(),
                output.prioridade(),
                output.observacoes(),
                output.status(),
                output.dataCriacao()
        );
    }
}
