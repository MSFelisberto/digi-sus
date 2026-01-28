package br.com.dgs.autenticacao.infrastructure.security.adapters;

import br.com.dgs.autenticacao.application.ports.outbound.TokenService;
import br.com.dgs.autenticacao.domain.funcionario.model.Funcionario;
import br.com.dgs.autenticacao.domain.paciente.model.Paciente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    @Value("${jwt.service.expiration:3600000}") // 1 hora para serviços
    private long serviceTokenExpiration;

    private SecretKey key;

    // Método para gerar token de serviço
    public String generateServiceToken(String serviceId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + serviceTokenExpiration);

        if (this.key == null) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

        return Jwts.builder()
                .setSubject(serviceId)
                .claim("userType", "SISTEMA")
                .claim("roles", List.of("ROLE_SISTEMA"))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateTokenForPaciente(Paciente paciente) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationInMs);

        if (this.key == null) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

        return Jwts.builder()
                .setSubject(paciente.getEmail().getValue())
                .claim("userId", paciente.getId().getValue())
                .claim("userType", "PACIENTE")
                .claim("roles", List.of("ROLE_PACIENTE"))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateTokenForFuncionario(Funcionario funcionario) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationInMs);

        if (this.key == null) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

        String roleStr = "ROLE_" + funcionario.getTipo().name();
        List<String> roles = List.of(roleStr);

        return Jwts.builder()
                .setSubject(funcionario.getEmail().getValue())
                .claim("userId", funcionario.getId().getValue())
                .claim("userType", "FUNCIONARIO")
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Métodos restantes mantidos iguais...
    @Override
    public boolean validateToken(String token) {
        try {
            getSecretKey();
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        getSecretKey();
        return getClaims(token).getSubject();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        getSecretKey();
        return getClaims(token).get("userId", Long.class);
    }

    @Override
    public String getUserTypeFromToken(String token) {
        getSecretKey();
        return getClaims(token).get("userType", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        getSecretKey();
        return getClaims(token).get("roles", List.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void getSecretKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
    }
}
