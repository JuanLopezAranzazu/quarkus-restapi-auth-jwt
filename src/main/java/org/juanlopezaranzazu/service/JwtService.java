package org.juanlopezaranzazu.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.access.duration", defaultValue = "900")
    Long accessTokenDuration;

    @ConfigProperty(name = "jwt.refresh.duration", defaultValue = "604800")
    Long refreshTokenDuration;

    public String generateAccessToken(String username, String userId, String... roles) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofSeconds(accessTokenDuration));

        return Jwt.issuer(issuer)
                .upn(username)
                .subject(userId)
                .groups(new HashSet<>(Arrays.asList(roles)))
                .issuedAt(now)
                .expiresAt(expiry)
                .jws()
                .sign();
    }

    public String generateRefreshToken(String username, String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofSeconds(refreshTokenDuration));

        return Jwt.issuer(issuer)
                .upn(username)
                .subject(userId)
                .claim("tokenType", "refresh")
                .claim("tokenId", UUID.randomUUID().toString())
                .issuedAt(now)
                .expiresAt(expiry)
                .jws()
                .sign();
    }

    public Long getAccessTokenDuration() {
        return accessTokenDuration;
    }

    public Long getRefreshTokenDuration() {
        return refreshTokenDuration;
    }
}
