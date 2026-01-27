package br.com.dgs.gateway.configuration;

import br.com.dgs.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;


@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouterValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (validator.isSecured.test(request)) {

                List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
                if (authHeaders == null || authHeaders.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
                }

                String authHeader = authHeaders.getFirst();
                String token = null;

                if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header format");
                }

                try {
                    if (StringUtils.hasText(token) && jwtUtil.isInvalid(token)) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or invalid");
                    }

                    String email = jwtUtil.getEmailFromToken(token);
                    List<String> roles = jwtUtil.getRolesFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    if (!StringUtils.hasText(email) || userId == null || roles == null || roles.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token data");
                    }

                    String rolesString = String.join(",", roles);

                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-ID", String.valueOf(userId))
                            .header("X-User-Email", email)
                            .header("X-User-Roles", rolesString)
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    return chain.filter(mutatedExchange);

                } catch (ResponseStatusException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to application: " + e.getMessage());
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
}