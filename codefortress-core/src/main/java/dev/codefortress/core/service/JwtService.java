package dev.codefortress.core.service;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.model.CodeFortressUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final CodeFortressProperties properties;

    public JwtService(CodeFortressProperties properties) {
        this.properties = properties;
    }

    public String generateToken(CodeFortressUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.roles());
        return createToken(claims, user.username());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + properties.getSecurity().getJwtExpirationMs()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecurity().getJwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostConstruct
    public void checkSecurity() {
        if ("default-super-secret-key-please-change-me-in-production-environment".equals(properties.getSecurity().getJwtSecret())) {
            log.warn("\n\n" +
                    "*************************************************************\n" +
                    "ATENCION: Estás usando la clave JWT por defecto de CodeFortress.\n" +
                    "Esto NO ES SEGURO para produccion.\n" +
                    "Configura 'codefortress.security.jwt-secret' en tu application.yml\n" +
                    "*************************************************************\n");
        }
    }
}