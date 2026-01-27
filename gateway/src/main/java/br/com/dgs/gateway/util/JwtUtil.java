package br.com.dgs.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey key;

    private void init() {
        if (key == null) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
    }

    public Claims getAllClaimsFromToken(String token) {
        init();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Object rolesObj = getAllClaimsFromToken(token).get("roles");

        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }

        if (rolesObj instanceof String) {
            return List.of((String) rolesObj);
        }

        return List.of();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public boolean isInvalid(String token) {
        try {
            return this.isTokenExpired(token);
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}