package com.bustling.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final Key jwtKey;
    private final long accessTokenExpirationInMs;
    private final long refreshTokenExpirationInMs;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(
            @Value("${app.jwtSecret}")
            String jwtSecret,
            @Value("${app.accessTokenExpirationInMs}")
            long accessTokenExpirationInMs,
            @Value("${app.refreshTokenExpirationInMs}")
            long refreshTokenExpirationInMs,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.accessTokenExpirationInMs = accessTokenExpirationInMs;
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationInMs);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtKey)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationInMs);

        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtKey)
                .compact();

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = getRefreshTokenKey(userId);
        ops.set(redisKey, getClaims(refreshToken).getId(), refreshTokenExpirationInMs, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateRefreshToken(String token, String userId) {
        try {
            Claims claims = getClaims(token);
            String redisKey = getRefreshTokenKey(userId);
            String storedVersion = redisTemplate.opsForValue().get(redisKey);

            return !isTokenExpired(token) && claims.getId().equals(storedVersion);
        } catch (Exception e) {
            return false;
        }
    }

    public void invalidateRefreshToken(String userId) {
        String redisKey = getRefreshTokenKey(userId);
        redisTemplate.delete(redisKey);
    }

    private String getRefreshTokenKey(String userId) {
        return "refreshToken:" + userId;
    }
}