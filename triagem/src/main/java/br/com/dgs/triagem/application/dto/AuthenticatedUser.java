package br.com.dgs.triagem.application.dto;

import java.util.List;

public record AuthenticatedUser(
        Long userId,
        String email,
        List<String> roles
) {
}
