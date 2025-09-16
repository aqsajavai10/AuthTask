package com.example.AuthTask.config;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret:secret-key-please-change}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}

