package br.com.dgs.autenticacao.infrastructure.security;

import br.com.dgs.autenticacao.infrastructure.security.adapters.TokenServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenService;

    public ServiceAuthenticationFilter(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            log.debug("Processando token de serviço para endpoint interno: {}", request.getRequestURI());

            if (StringUtils.hasText(jwt) && tokenService.validateToken(jwt)) {
                String userType = tokenService.getUserTypeFromToken(jwt);

                if ("SISTEMA".equals(userType)) {
                    List<String> roles = tokenService.getRolesFromToken(jwt);
                    String serviceId = tokenService.getEmailFromToken(jwt);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(serviceId, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Serviço autenticado: {}", serviceId);
                }
            } else {
                log.warn("Token inválido ou ausente para endpoint interno: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("Erro ao processar token de serviço: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
