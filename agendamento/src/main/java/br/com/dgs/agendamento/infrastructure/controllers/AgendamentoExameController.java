package br.com.dgs.agendamento.infrastructure.controllers;

import br.com.dgs.agendamento.application.dto.*;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoExameUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exames")
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
        AgendaExameResponseDTO response = toAgendaResponse(output);

        URI uri = URI.create("/agendamento/exames/agenda/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/agenda/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativarAgendaExame(@PathVariable Long id) {
        agendamentoExameUseCase.desativarAgendaExame(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/agenda/{id}/gerar-horarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> gerarHorarios(
            @PathVariable Long id,
            @RequestBody @Valid GerarHorariosRequestDTO requestDTO) {

        GerarHorariosExameCommand command = new GerarHorariosExameCommand(
                id,
                requestDTO.dataInicio(),
                requestDTO.dataFim()
        );

        agendamentoExameUseCase.gerarHorariosExame(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/horarios/disponiveis")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HorarioExameDisponivelResponseDTO>> buscarDisponiveis(
            @RequestParam Long tipoExameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        BuscarHorariosExameQuery query = new BuscarHorariosExameQuery(tipoExameId, dataInicio, dataFim);

        List<HorarioExameDisponivelResponseDTO> response = agendamentoExameUseCase
                .buscarHorariosExameDisponiveis(query)
                .stream()
                .map(this::toHorarioResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/agendamentos")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ATENDENTE', 'ADMIN')")
    public ResponseEntity<AgendamentoExameResponseDTO> agendarExame(
            @RequestBody @Valid AgendarExameRequestDTO requestDTO) {

        AgendarExameCommand command = new AgendarExameCommand(
                requestDTO.horarioExameDisponivelId(),
                requestDTO.solicitacaoExameId()
        );

        AgendamentoExameOutput output = agendamentoExameUseCase.agendarExame(command);
        AgendamentoExameResponseDTO response = toAgendamentoResponse(output);

        URI uri = URI.create("/agendamento/exames/agendamentos/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/agendamentos/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ADMIN')")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable Long id) {
        agendamentoExameUseCase.cancelarAgendamentoExame(id);
        return ResponseEntity.noContent().build();
    }

    private AgendaExameResponseDTO toAgendaResponse(AgendaExameOutput output) {
        return new AgendaExameResponseDTO(
                output.id(), output.tipoExameId(), output.diaSemana(),
                output.horaInicio(), output.horaFim(),
                output.duracaoSlotMinutos(), output.vagasPorSlot(), output.ativa()
        );
    }

    private HorarioExameDisponivelResponseDTO toHorarioResponse(HorarioExameDisponivelOutput output) {
        return new HorarioExameDisponivelResponseDTO(
                output.id(), output.tipoExameId(), output.dataHora(), output.vagasRestantes()
        );
    }

    private AgendamentoExameResponseDTO toAgendamentoResponse(AgendamentoExameOutput output) {
        return new AgendamentoExameResponseDTO(
                output.id(), output.horarioExameId(), output.solicitacaoExameId(),
                output.tipoExameId(), output.dataHora(), output.status(), output.dataCriacao()
        );
    }
}
