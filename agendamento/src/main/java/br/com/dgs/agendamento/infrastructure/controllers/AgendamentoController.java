package br.com.dgs.agendamento.infrastructure.controllers;

import br.com.dgs.agendamento.application.dto.*;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.ConsultaRequestDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.ConsultaResponseDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.ReagendarConsultaRequestDTO;
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
@RequestMapping
public class AgendamentoController {

    private final AgendamentoUseCase agendamentoUseCase;

    public AgendamentoController(AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<ConsultaResponseDTO> agendar(@RequestBody @Valid ConsultaRequestDTO requestDTO) {

        AgendarConsultaCommand command = new AgendarConsultaCommand(
                requestDTO.pacienteId(),
                requestDTO.medicoId(),
                requestDTO.dataHora(),
                requestDTO.especialidade()
        );

        ConsultaOutput output = agendamentoUseCase.agendarConsulta(command);
        ConsultaResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/agendamento/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<ConsultaResponseDTO> reagendar(
            @PathVariable Long id,
            @RequestBody @Valid ReagendarConsultaRequestDTO requestDTO) {

        ReagendarConsultaCommand command = new ReagendarConsultaCommand(
                id,
                requestDTO.medicoId(),
                requestDTO.dataHora(),
                requestDTO.especialidade()
        );

        ConsultaOutput output = agendamentoUseCase.reagendarConsulta(command);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        CancelarConsultaCommand command = new CancelarConsultaCommand(id);
        agendamentoUseCase.cancelarConsulta(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorPaciente(
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

        ListarConsultasQuery query = new ListarConsultasQuery(pacienteId, currentUser);
        List<ConsultaResponseDTO> response = agendamentoUseCase.listarConsultasPorPaciente(query)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private ConsultaResponseDTO toResponseDTO(ConsultaOutput output) {
        return new ConsultaResponseDTO(
                output.id(),
                output.pacienteId(),
                output.medicoId(),
                output.dataHora(),
                output.especialidade(),
                output.status()
        );
    }
}
