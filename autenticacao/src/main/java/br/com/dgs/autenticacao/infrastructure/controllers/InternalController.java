package br.com.dgs.autenticacao.infrastructure.controllers;

import br.com.dgs.autenticacao.application.dto.ValidarPacienteQuery;
import br.com.dgs.autenticacao.application.ports.inbound.PacienteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final PacienteUseCase pacienteUseCase;

    public InternalController(PacienteUseCase pacienteUseCase) {
        this.pacienteUseCase = pacienteUseCase;
    }

    @GetMapping("/usuarios/pacientes/{pacienteId}/exists")
    @PreAuthorize("hasRole('SISTEMA')")
    public ResponseEntity<Boolean> existePaciente(@PathVariable Long pacienteId) {
        ValidarPacienteQuery query = new ValidarPacienteQuery(pacienteId);
        boolean exists = pacienteUseCase.validarPacienteExiste(query);
        return ResponseEntity.ok(exists);
    }
}
