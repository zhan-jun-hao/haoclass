package com.haoclass.gateway.security;

import com.haoclass.common.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * gateway里面的jwt解析器
 */
@Component
@RequiredArgsConstructor
public class GatewayJwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 将token转化为用户上下文
     * @param token
     * @return
     */
    public UserContext parseUserContext(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new UserContext(
                getLongClaim(claims, "userId"),
                getIntegerClaim(claims, "role"),
                getLongClaim(claims, "authVersion")
        );
    }

    public String getHeader() {
        return jwtProperties.getHeader();
    }

    public String getTokenPrefix() {
        return jwtProperties.getTokenPrefix();
    }

    private Long getLongClaim(Claims claims, String userId) {
        Object value = claims.get(userId);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("JWT claim is missing: " + userId);
        }
        return number.longValue();
    }

    private Integer getIntegerClaim(Claims claims, String role) {
        Object value = claims.get(role);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("JWT claim is missing: " + role);
        }
        return number.intValue();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
