package br.com.dgs.agendamento.infrastructure.controllers.exame;

import br.com.dgs.agendamento.application.dto.exame.CriarTipoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.TipoExameOutput;
import br.com.dgs.agendamento.application.ports.inbound.TipoExameUseCase;
import br.com.dgs.agendamento.infrastructure.controllers.dto.exame.TipoExameRequestDTO;
import br.com.dgs.agendamento.infrastructure.controllers.dto.exame.TipoExameResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exames/tipos")
public class TipoExameController {

    private final TipoExameUseCase tipoExameUseCase;

    public TipoExameController(TipoExameUseCase tipoExameUseCase) {
        this.tipoExameUseCase = tipoExameUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoExameResponseDTO> criar(
            @RequestBody @Valid TipoExameRequestDTO requestDTO) {

        CriarTipoExameCommand command = new CriarTipoExameCommand(
                requestDTO.nome(),
                requestDTO.codigo(),
                requestDTO.descricao(),
                requestDTO.preparacao()
        );

        TipoExameOutput output = tipoExameUseCase.criarTipoExame(command);
        TipoExameResponseDTO response = toResponseDTO(output);

        URI uri = URI.create("/agendamento/exames/tipos/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TipoExameResponseDTO>> listarTodos() {
        List<TipoExameResponseDTO> response = tipoExameUseCase.listarTodos()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TipoExameResponseDTO> buscarPorId(@PathVariable Long id) {
        TipoExameOutput output = tipoExameUseCase.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(output));
    }

    private TipoExameResponseDTO toResponseDTO(TipoExameOutput output) {
        return new TipoExameResponseDTO(
                output.id(),
                output.nome(),
                output.codigo(),
                output.descricao(),
                output.preparacao(),
                output.ativo()
        );
    }
}