package com.hungum.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveVerificationToken(String token, String username, Duration ttl) {
        redisTemplate.opsForValue().set(token, username, ttl);
    }

    public Optional<String> getUsernameByToken(String token) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(token));
    }

    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
    public void saveRefreshToken(String username, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken, ttl);
    }

    public Optional<String> getRefreshToken(String username) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("refresh:" + username));
    }
}


