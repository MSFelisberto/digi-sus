package br.com.dgs.agendamento.infrastructure.controllers;

import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.ports.inbound.AgendamentoUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.ConsultaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final AgendamentoUseCase agendamentoUseCase;

    public InternalController(AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarConsultaPorId(@PathVariable Long id) {
        ConsultaOutput output = agendamentoUseCase.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    @PatchMapping("/consultas/{id}/realizada")
    public ResponseEntity<ConsultaResponseDTO> marcarComoRealizada(@PathVariable Long id) {
        ConsultaOutput output = agendamentoUseCase.marcarComoRealizada(id);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    private ConsultaResponseDTO toResponseDTO(ConsultaOutput output) {
        return new ConsultaResponseDTO(
                output.id(),
                output.pacienteId(),
                output.medicoId(),
                output.dataHora(),
                output.especialidade(),
                output.status(),
                output.tipoConsulta(),
                output.prioridade(),
                output.triagemId()
        );
    }
}
