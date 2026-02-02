package br.com.dgs.agendamento.infrastructure.controllers;

import br.com.dgs.agendamento.application.dto.AutoAgendarCommand;
import br.com.dgs.agendamento.application.dto.AuthenticatedUser;
import br.com.dgs.agendamento.application.dto.BuscarHorariosQuery;
import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.HorarioDisponivelOutput;
import br.com.dgs.agendamento.application.ports.inbound.HorarioDisponivelUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.AutoAgendarRequestDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.ConsultaResponseDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.HorarioDisponivelResponseDTO;
import br.com.dgs.agendamento.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.agendamento.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/horarios")
public class HorarioDisponivelController {

    private final HorarioDisponivelUseCase horarioDisponivelUseCase;

    public HorarioDisponivelController(HorarioDisponivelUseCase horarioDisponivelUseCase) {
        this.horarioDisponivelUseCase = horarioDisponivelUseCase;
    }

    @GetMapping("/disponiveis")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HorarioDisponivelResponseDTO>> buscarDisponiveis(
            @RequestParam String especialidade,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        BuscarHorariosQuery query = new BuscarHorariosQuery(especialidade, dataInicio, dataFim);

        List<HorarioDisponivelResponseDTO> response = horarioDisponivelUseCase
                .buscarHorariosDisponiveis(query)
                .stream()
                .map(this::toHorarioResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/autoagendamento")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ATENDENTE')")
    public ResponseEntity<ConsultaResponseDTO> autoAgendar(
            @RequestBody @Valid AutoAgendarRequestDTO requestDTO,
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

        AutoAgendarCommand command = new AutoAgendarCommand(
                requestDTO.horarioDisponivelId(),
                currentUser
        );

        ConsultaOutput output = horarioDisponivelUseCase.autoAgendar(command);

        ConsultaResponseDTO response = new ConsultaResponseDTO(
                output.id(),
                output.pacienteId(),
                output.medicoId(),
                output.dataHora(),
                output.especialidade(),
                output.status()
        );

        URI uri = URI.create("/agendamento/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    private HorarioDisponivelResponseDTO toHorarioResponseDTO(HorarioDisponivelOutput output) {
        return new HorarioDisponivelResponseDTO(
                output.id(),
                output.medicoId(),
                output.dataHora(),
                output.especialidade()
        );
    }
}