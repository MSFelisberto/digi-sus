package br.com.dgs.exames.infrastructure.controllers;

import br.com.dgs.exames.application.dto.CriarTipoExameCommand;
import br.com.dgs.exames.application.dto.TipoExameOutput;
import br.com.dgs.exames.application.ports.inbound.TipoExameUseCase;
import br.com.dgs.exames.infrastructure.controllers.dto.TipoExameRequestDTO;
import br.com.dgs.exames.infrastructure.controllers.dto.TipoExameResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tipos")
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

        URI uri = URI.create("/exames/tipos/" + response.id());
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
