package br.com.dgs.agendamento.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    private final String jwtSecret;

    public UserRoleAuthenticationFilter(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userIdStr = request.getHeader("X-User-ID");
        String userEmail = request.getHeader("X-User-Email");
        String userRoles = request.getHeader("X-User-Roles");

        // Primeiro tenta autenticar via headers X-User-* (requisições via Gateway)
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
            // Se não tem headers X-User-*, tenta autenticar via token JWT Bearer (service-to-service)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String subject = claims.getSubject();
                    String userType = claims.get("userType", String.class);
                    @SuppressWarnings("unchecked")
                    List<String> roles = claims.get("roles", List.class);

                    if (roles != null && !roles.isEmpty()) {
                        List<GrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        // Para tokens de serviço, o userId pode não existir
                        Long userId = claims.get("userId", Long.class);
                        if (userId == null) {
                            userId = 0L; // ID especial para serviços
                        }

                        UserPrincipal principal = new UserPrincipal(userId, subject, authorities);
                        JwtAuthenticationToken auth = new JwtAuthenticationToken(principal, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        logger.debug("Token JWT validado com sucesso para: " + subject + " com tipo: " + userType);
                    }
                } catch (Exception e) {
                    logger.warn("UserRoleAuthenticationFilter: falha ao validar token JWT: " + e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            } else {
                logger.warn("UserRoleAuthenticationFilter: não conseguiu resgatar o email e role do usuario. Limpando o contexto");
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
