package br.com.dgs.exames.application.dto;

import java.util.Collection;
import java.util.Set;

public class AuthenticatedUser {

    private final Long id;
    private final String userEmail;
    private final Set<String> roles;

    public AuthenticatedUser(Long id, String userEmail, Collection<String> roles) {
        this.id = id;
        this.userEmail = userEmail;
        this.roles = Set.copyOf(roles);
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains("ROLE_" + role);
    }
}
