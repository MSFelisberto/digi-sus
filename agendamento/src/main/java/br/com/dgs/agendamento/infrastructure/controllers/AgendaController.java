package br.com.dgs.agendamento.infrastructure.controllers;

import br.com.dgs.agendamento.application.dto.AgendaOutput;
import br.com.dgs.agendamento.application.dto.AuthenticatedUser;
import br.com.dgs.agendamento.application.dto.CriarAgendaCommand;
import br.com.dgs.agendamento.application.dto.GerarHorariosCommand;
import br.com.dgs.agendamento.application.ports.inbound.AgendaUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.AgendaRequestDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.AgendaResponseDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.GerarHorariosRequestDTO;
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
@RequestMapping("/agenda")
public class AgendaController {

    private final AgendaUseCase agendaUseCase;

    public AgendaController(AgendaUseCase agendaUseCase) {
        this.agendaUseCase = agendaUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<AgendaResponseDTO> criarAgenda(
            @RequestBody @Valid AgendaRequestDTO requestDTO,
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

        CriarAgendaCommand command = new CriarAgendaCommand(
                requestDTO.medicoId(),
                requestDTO.diaSemana(),
                requestDTO.horaInicio(),
                requestDTO.horaFim(),
                requestDTO.duracaoSlotMinutos(),
                requestDTO.especialidade(),
                currentUser
        );

        AgendaOutput output = agendaUseCase.criarAgenda(command);
        AgendaResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/agendamento/agenda/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<Void> desativarAgenda(@PathVariable Long id) {
        agendaUseCase.desativarAgenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<List<AgendaResponseDTO>> listarAgendasMedico(
            @PathVariable Long medicoId) {

        List<AgendaResponseDTO> response = agendaUseCase.listarAgendasPorMedico(medicoId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/gerar-horarios")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<Void> gerarHorarios(
            @PathVariable Long id,
            @RequestBody @Valid GerarHorariosRequestDTO requestDTO) {

        GerarHorariosCommand command = new GerarHorariosCommand(
                id,
                requestDTO.dataInicio(),
                requestDTO.dataFim()
        );

        agendaUseCase.gerarHorarios(command);
        return ResponseEntity.noContent().build();
    }

    private AgendaResponseDTO toResponseDTO(AgendaOutput output) {
        return new AgendaResponseDTO(
                output.id(),
                output.medicoId(),
                output.diaSemana(),
                output.horaInicio(),
                output.horaFim(),
                output.duracaoSlotMinutos(),
                output.especialidade(),
                output.ativa()
        );
    }
}