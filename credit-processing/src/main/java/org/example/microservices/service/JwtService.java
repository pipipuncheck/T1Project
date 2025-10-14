package org.example.microservices.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);
    private static final String SECRET_KEY = "10DC8CBB25F7F9D82ABADD3991D455D5D9731F6902761376A014E8E92BF03645";

    public String generateServiceToken() {
        UserDetails serviceUser = User.builder()
                .username("ms3-service")
                .password("")
                .authorities("ROLE_MASTER")
                .build();

        return Jwts.builder()
                .subject(serviceUser.getUsername())
                .claim("roles", serviceUser.getAuthorities())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
