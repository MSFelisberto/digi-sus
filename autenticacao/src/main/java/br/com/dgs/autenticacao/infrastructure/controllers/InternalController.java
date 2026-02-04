package br.com.dgs.autenticacao.infrastructure.controllers;

import br.com.dgs.autenticacao.application.dto.FuncionarioOutput;
import br.com.dgs.autenticacao.application.dto.ValidarPacienteQuery;
import br.com.dgs.autenticacao.application.ports.inbound.FuncionarioUseCase;
import br.com.dgs.autenticacao.application.ports.inbound.PacienteUseCase;
import br.com.dgs.autenticacao.domain.funcionario.model.TipoFuncionario;
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
    private final FuncionarioUseCase funcionarioUseCase;

    public InternalController(PacienteUseCase pacienteUseCase,
                              FuncionarioUseCase funcionarioUseCase) {
        this.pacienteUseCase = pacienteUseCase;
        this.funcionarioUseCase = funcionarioUseCase;
    }

    @GetMapping("/usuarios/pacientes/{pacienteId}/exists")
    @PreAuthorize("hasRole('SISTEMA')")
    public ResponseEntity<Boolean> existePaciente(@PathVariable Long pacienteId) {
        ValidarPacienteQuery query = new ValidarPacienteQuery(pacienteId);
        boolean exists = pacienteUseCase.validarPacienteExiste(query);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/usuarios/funcionarios/{funcionarioId}/is-medico")
    @PreAuthorize("hasRole('SISTEMA')")
    public ResponseEntity<Boolean> isMedico(@PathVariable Long funcionarioId) {
        try {
            FuncionarioOutput funcionario = funcionarioUseCase.buscarPorId(funcionarioId);
            return ResponseEntity.ok(funcionario.tipo() == TipoFuncionario.MEDICO);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
