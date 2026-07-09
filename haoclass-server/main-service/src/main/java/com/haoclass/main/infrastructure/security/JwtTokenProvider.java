package com.haoclass.main.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(LoginUser loginUser, Long authVersion) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + jwtProperties.getExpireSeconds() * 1000);
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(loginUser.getUsername())
                .claim("userId", loginUser.getId())
                .claim("role", loginUser.getRole())
                .claim("authVersion", authVersion)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getAuthVersion(String token) {
        Object value = parseClaims(token).get("authVersion");
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("JWT claim is missing: authVersion");
        }
        return number.longValue();
    }

    public boolean validateToken(String token, String username) {
        Claims claims = parseClaims(token);
        return username.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
    }

    public Long getExpireSeconds() {
        return jwtProperties.getExpireSeconds();
    }

    public String getHeader() {
        return jwtProperties.getHeader();
    }

    public String getTokenPrefix() {
        return jwtProperties.getTokenPrefix();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
