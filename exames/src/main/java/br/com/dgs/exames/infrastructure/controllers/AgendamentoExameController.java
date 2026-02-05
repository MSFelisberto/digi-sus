package br.com.dgs.exames.infrastructure.controllers;

import br.com.dgs.exames.application.dto.*;
import br.com.dgs.exames.application.ports.inbound.AgendamentoExameUseCase;
import br.com.dgs.exames.infrastructure.controllers.dto.*;
import br.com.dgs.exames.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.exames.infrastructure.security.UserPrincipal;
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
@RequestMapping("/agendamentos")
public class AgendamentoExameController {

    private final AgendamentoExameUseCase agendamentoExameUseCase;

    public AgendamentoExameController(AgendamentoExameUseCase agendamentoExameUseCase) {
        this.agendamentoExameUseCase = agendamentoExameUseCase;
    }

    @PostMapping("/agenda")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgendaExameResponseDTO> criarAgendaExame(
            @RequestBody @Valid AgendaExameRequestDTO requestDTO) {

        CriarAgendaExameCommand command = new CriarAgendaExameCommand(
                requestDTO.tipoExameId(),
                requestDTO.diaSemana(),
                requestDTO.horaInicio(),
                requestDTO.horaFim(),
                requestDTO.duracaoSlotMinutos(),
                requestDTO.vagasPorSlot()
        );

        AgendaExameOutput output = agendamentoExameUseCase.criarAgendaExame(command);
        AgendaExameResponseDTO response = toAgendaResponseDTO(output);

        URI uri = URI.create("/exames/agendamentos/agenda/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/vagas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VagaExameResponseDTO>> buscarVagas(
            @RequestParam Long tipoExameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        BuscarVagasExameQuery query = new BuscarVagasExameQuery(tipoExameId, dataInicio, dataFim);

        List<VagaExameResponseDTO> response = agendamentoExameUseCase.buscarVagasDisponiveis(query)
                .stream()
                .map(this::toVagaResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'ATENDENTE', 'ADMIN')")
    public ResponseEntity<AgendamentoExameResponseDTO> agendarExame(
            @RequestBody @Valid AgendarExameRequestDTO requestDTO,
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

        AgendarExameCommand command = new AgendarExameCommand(
                requestDTO.solicitacaoExameId(),
                requestDTO.dataHora(),
                currentUser
        );

        AgendamentoExameOutput output = agendamentoExameUseCase.agendarExame(command);
        AgendamentoExameResponseDTO response = toAgendamentoResponseDTO(output);

        URI uri = URI.create("/exames/agendamentos/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ADMIN')")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable Long id) {
        agendamentoExameUseCase.cancelarAgendamento(id);
        return ResponseEntity.noContent().build();
    }

    private AgendaExameResponseDTO toAgendaResponseDTO(AgendaExameOutput output) {
        return new AgendaExameResponseDTO(
                output.id(),
                output.tipoExameId(),
                output.diaSemana(),
                output.horaInicio(),
                output.horaFim(),
                output.duracaoSlotMinutos(),
                output.vagasPorSlot(),
                output.ativa()
        );
    }

    private VagaExameResponseDTO toVagaResponseDTO(VagaExameOutput output) {
        return new VagaExameResponseDTO(
                output.dataHora(),
                output.vagasRestantes(),
                output.tipoExameId()
        );
    }

    private AgendamentoExameResponseDTO toAgendamentoResponseDTO(AgendamentoExameOutput output) {
        return new AgendamentoExameResponseDTO(
                output.id(),
                output.solicitacaoExameId(),
                output.tipoExameId(),
                output.dataHora(),
                output.status(),
                output.dataCriacao()
        );
    }
}
