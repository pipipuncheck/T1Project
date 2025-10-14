package org.example.microservices.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);
    private static final String SECRET_KEY = "10DC8CBB25F7F9D82ABADD3991D455D5D9731F6902761376A014E8E92BF03645";

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return (Objects.equals(username, user.getUsername()) && !isTokenExpired(token));
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public String extractUsername(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        return claims.getSubject();
    }

    private Date extractExpiration(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        return claims.getExpiration();
    }


    private SecretKey generateKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
