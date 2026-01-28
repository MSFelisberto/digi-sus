package br.com.dgs.agendamento.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userIdStr = request.getHeader("X-User-ID");
        String userEmail = request.getHeader("X-User-Email");
        String userRoles = request.getHeader("X-User-Roles");

        if (userEmail != null && userRoles != null && !userRoles.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr);
                List<GrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserPrincipal principal = new UserPrincipal(userId, userEmail, authorities);

                JwtAuthenticationToken auth = new JwtAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                logger.error("UserRoleAuthenticationFilter: não conseguiu validar o authorities do usuario", e);
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.warn("UserRoleAuthenticationFilter: não conseguiu resgatar o email e role do usuario. Limpando o contexto");
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
