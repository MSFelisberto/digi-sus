package br.com.dgs.atendimento.application.dto;

import java.util.List;

public record AuthenticatedUser(
        Long id,
        String email,
        List<String> roles
) {
    public boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return roles.contains(roleWithPrefix);
    }
}
