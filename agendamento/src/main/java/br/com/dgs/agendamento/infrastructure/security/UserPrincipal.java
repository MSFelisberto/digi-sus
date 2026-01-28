package br.com.dgs.agendamento.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean hasRole(String role) {
        return this.authorities
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}