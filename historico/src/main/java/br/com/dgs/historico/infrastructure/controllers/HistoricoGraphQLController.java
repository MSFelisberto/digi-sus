package br.com.dgs.historico.infrastructure.controllers;

import br.com.dgs.historico.application.dto.AuthenticatedUser;
import br.com.dgs.historico.application.dto.HistoricoOutput;
import br.com.dgs.historico.application.dto.ListarHistoricoQuery;
import br.com.dgs.historico.application.ports.inbound.HistoricoUseCase;
import br.com.dgs.historico.infrastructure.security.JwtAuthenticationToken;
import br.com.dgs.historico.infrastructure.security.UserPrincipal;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HistoricoGraphQLController {

    private final HistoricoUseCase historicoUseCase;

    public HistoricoGraphQLController(HistoricoUseCase historicoUseCase) {
        this.historicoUseCase = historicoUseCase;
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoOutput> historicoPorPaciente(
            @Argument Long pacienteId,
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

        ListarHistoricoQuery query = new ListarHistoricoQuery(pacienteId, currentUser);
        return historicoUseCase.listarHistoricoPorPaciente(query);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public HistoricoOutput historicoPorConsulta(@Argument Long consultaId) {
        return historicoUseCase.buscarPorConsultaId(consultaId);
    }
}
