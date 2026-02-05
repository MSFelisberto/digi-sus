package br.com.dgs.atendimento.infrastructure.controllers;

import br.com.dgs.atendimento.application.dto.*;
import br.com.dgs.atendimento.application.ports.inbound.AtendimentoUseCase;
import br.com.dgs.atendimento.infrastructure.controllers.dto.*;
import br.com.dgs.atendimento.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.atendimento.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AtendimentoUseCase atendimentoUseCase;

    public AtendimentoController(AtendimentoUseCase atendimentoUseCase) {
        this.atendimentoUseCase = atendimentoUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<AtendimentoResponseDTO> iniciarAtendimento(
            @RequestBody @Valid IniciarAtendimentoRequestDTO requestDTO,
            Authentication authentication) {

        UserPrincipal principal = ((JwtAuthenticationToken) authentication).getPrincipal();

        IniciarAtendimentoCommand command = new IniciarAtendimentoCommand(
                requestDTO.consultaId(),
                principal.getId()
        );

        AtendimentoOutput output = atendimentoUseCase.iniciarAtendimento(command);
        AtendimentoResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/atendimentos/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<AtendimentoResponseDTO> finalizarAtendimento(
            @PathVariable Long id,
            @RequestBody @Valid FinalizarAtendimentoRequestDTO requestDTO) {

        FinalizarAtendimentoCommand command = new FinalizarAtendimentoCommand(
                id,
                requestDTO.anamnese(),
                requestDTO.condutaMedica()
        );

        AtendimentoOutput output = atendimentoUseCase.finalizarAtendimento(command);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    @PostMapping("/{id}/exames")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Map<String, String>> solicitarExame(
            @PathVariable Long id,
            @RequestBody @Valid SolicitarExameRequestDTO requestDTO) {

        SolicitarExameCommand command = new SolicitarExameCommand(
                id,
                requestDTO.tipoExame(),
                requestDTO.prioridade(),
                requestDTO.observacoes()
        );

        atendimentoUseCase.solicitarExame(command);

        return ResponseEntity.accepted().body(Map.of(
                "message", "Solicitação de exame enviada para processamento",
                "tipoExame", requestDTO.tipoExame()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<AtendimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        AtendimentoOutput output = atendimentoUseCase.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<AtendimentoResponseDTO> buscarPorConsultaId(@PathVariable Long consultaId) {
        AtendimentoOutput output = atendimentoUseCase.buscarPorConsultaId(consultaId);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    private AtendimentoResponseDTO toResponseDTO(AtendimentoOutput output) {
        return new AtendimentoResponseDTO(
                output.id(),
                output.consultaId(),
                output.pacienteId(),
                output.medicoId(),
                output.anamnese(),
                output.condutaMedica(),
                output.dataHoraInicio(),
                output.dataHoraFim(),
                output.status()
        );
    }
}
