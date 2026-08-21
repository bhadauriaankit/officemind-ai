package com.officemind.infrastructure.security;

import com.officemind.application.user.SessionRevocationPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisSessionRevocationAdapter implements SessionRevocationPort {

    private static final String KEY_PREFIX = "officemind:revoked-jti:";

    private final StringRedisTemplate redisTemplate;

    public RedisSessionRevocationAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void revoke(String tokenId, Duration remainingValidity) {
        redisTemplate.opsForValue().set(key(tokenId), "revoked", remainingValidity);
    }

    @Override
    public boolean isRevoked(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(tokenId)));
    }

    private String key(String tokenId) {
        return KEY_PREFIX + tokenId;
    }
}
