package br.com.dgs.historico.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String userIdStr = request.getHeader("X-User-ID");
        String userEmail = request.getHeader("X-User-Email");
        String userRoles = request.getHeader("X-User-Roles");

        log.debug("Headers recebidos - ID: {}, Email: {}, Roles: {}", userIdStr, userEmail, userRoles);

        if (userIdStr != null && userEmail != null && userRoles != null && !userRoles.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr);

                List<GrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserPrincipal principal = new UserPrincipal(userId, userEmail, authorities);
                JwtAuthenticationToken auth = new JwtAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

                log.debug("Usuário autenticado com sucesso - ID: {}, Email: {}, Authorities: {}",
                        userId, userEmail, authorities);

            } catch (NumberFormatException e) {
                log.error("Erro ao converter X-User-ID para Long: {}", userIdStr, e);
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("Erro ao processar autenticação: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.warn("Headers de autenticação ausentes ou incompletos. ID: {}, Email: {}, Roles: {}",
                    userIdStr, userEmail, userRoles);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
